package com.bsp.dnb.serviceImpl;

import com.bsp.dnb.dto.PayslipDto;
import com.bsp.dnb.dto.PayslipSearchDto;
import com.bsp.dnb.entity.*;
import com.bsp.dnb.exception.ResourceNotFoundException;
import com.bsp.dnb.repo.*;
import com.bsp.dnb.service.DnbRoleService;
import com.bsp.dnb.service.PayslipService;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * PayslipServiceImpl
 *
 * Generates a PDF payslip that matches the layout shown in the screenshot (JLN
 * Hospital / SAIL-Bhilai Steel Plant Stipend Statement) and populates all
 * fields according to the dnbpayslip_a4.sql query.
 */
@Service
@Slf4j
public class PayslipServiceImpl implements PayslipService {

	/* ── Repositories ──────────────────────────────────────────────────── */
	@Autowired
	private DnbMastRepository dnbMastRepository;
	
	@Autowired
	private DnbAttRepository dnbAttRepository;
	
	@Autowired
	private DnbPbillRepository dnbPaybillRepository;
	@Autowired
	private DnbCumRepository dnbCumRepository;
	 
	@Autowired
	private DnbRoleService dnbRoleService;
	
	@Autowired
	private CategoryRepository categoryRepository;

	/* ── Font constants ────────────────────────────────────────────────── */
	private static final Font FONT_HEADER_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
	private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
	private static final Font FONT_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
	private static final Font FONT_SMALL = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
	private static final Font FONT_LABEL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);
	private static final Font FONT_VALUE = FontFactory.getFont(FontFactory.COURIER, 8, Color.BLACK);

	/*
	 * ═══════════════════════════════════════════════════════════════════ PUBLIC
	 * API ═══════════════════════════════════════════════════════════════════
	 */
 
	@Override
	public PayslipDto getPayslip(Integer yymm, Integer id) {

		DnbMast mast = getMast(id);
		DnbAtt att = dnbAttRepository.findAttendance(yymm, id).orElse(null);
		DnbPbill paybill = getPaybill(yymm, id);
		DnbCum cum = dnbCumRepository.findById(new DnbCumId(yymm, id)).orElse(null);

		return buildDto(mast, att, paybill, cum, yymm);
	}

	/**
	 * Generates and returns a PDF byte array for download.
	 */
	@Override
	public byte[] generatePayslip(Integer yymm, Integer id) {

	    log.info("Generating payslip for id={} yymm={}", id, yymm);

	    try {

	        DnbMast mast = getMast(id);
	        if (mast == null) {
	            throw new ResourceNotFoundException("DNBMAST record not found for id=" + id);
	        }

	        DnbAtt att = dnbAttRepository.findAttendance(id, yymm)
	                .orElseThrow(() ->
	                        new ResourceNotFoundException("Attendance not found for id=" + id + ", yymm=" + yymm));

	        DnbPbill paybill = getPaybill(yymm, id);
	        if (paybill == null) {
	            throw new ResourceNotFoundException("Paybill not found for id=" + id + ", yymm=" + yymm);
	        }

	        DnbCum cum = dnbCumRepository.findFirstByIdAndYymm(id, yymm);
	        if (cum == null) {
	            throw new ResourceNotFoundException("DnbCum not found for id=" + id + ", yymm=" + yymm);
	        }

	        PayslipDto dto = buildDto(mast, att, paybill, cum, yymm);

	        return buildPdf(dto);

	    } catch (Exception ex) {

	        log.error("Payslip generation failed: {}", ex.getMessage(), ex);

	        throw new RuntimeException(ex.getMessage(), ex);
	    }
	}

	/*
	 * ═══════════════════════════════════════════════════════════════════ DTO
	 * BUILDER – mirrors dnbpayslip_a4.sql field by field
	 * ═══════════════════════════════════════════════════════════════════
	 */

	private PayslipDto buildDto(DnbMast mast, DnbAtt att, DnbPbill pb, DnbCum cum, Integer yymm) {

		// -- TDS = itaxrec + cessrec + cessaddl (line14 in SQL)
		BigDecimal tds = safeDecimal(pb.getItaxrec()).add(safeDecimal(pb.getCessrec()))
				.add(safeDecimal(pb.getCessaddl()));

		// -- Cum Tax = CUM_ITAX + CUM_CESS + CUM_CESS_ADDL (line28 in SQL)
		BigDecimal cumTax = cum == null ? BigDecimal.ZERO
				: safeDecimal(cum.getCumItax()).add(safeDecimal(cum.getCumCess()))
						.add(safeDecimal(cum.getCumCessAddl()));

		// -- Cum Savings = cum_sav_80ccc + cum_sav_80ccf (line28 in SQL)
		BigDecimal cumSavings = cum == null ? BigDecimal.ZERO
				: safeDecimal(cum.getCumSav80ccc()).add(safeDecimal(cum.getCumSav80ccf()));

		// -- catg 5 or 6 → show tuition message (decode in SQL lines 33/34)
		boolean showTuition = mast.getCatg() != null && (mast.getCatg() == 5 || mast.getCatg() == 6);

		// -- Bank display: lpad(bank_cd,3,'0') || '/' || lpad(bank_acno,16,' ')
		String bankCd = lpad(String.valueOf(mast.getBankCd()), 3, '0');

		// -- Month/Year: to_char(to_date(a.yymm,'yyyymm'),'MON-YYYY')
		String mthYr = formatYymm(yymm);

		return PayslipDto.builder().id(mast.getId()).yymm(yymm).mthYr(mthYr)
				// employee master
				.name(mast.getName()).pan(nvl(mast.getPan(), "")).bankCd(bankCd).bankAcno(nvl(mast.getBankAcno(), ""))
				.catg(mast.getCatg()).catgDesc(nvl(mast.getCatgDesc(), "")).doj(formatDate(mast.getDoj()))
				.dos(formatDate(mast.getDos()))
				// rate / attendance
				.stipendRate(safeDecimal(mast.getStipendRate())).dailyRate(safeDecimal(mast.getDailyRate()))
				.duty(att == null ? 0 : nvlInt(att.getDuty()))
				// current month
				.stipend(safeDecimal(pb.getStipend())).adj(pb.getAdj() == null ? BigDecimal.ZERO : safeDecimal(pb.getAdj()))
				.tds(tds).grossPay(safeDecimal(pb.getGpay())).netPay(safeDecimal(pb.getNpay()))
				// cumulative
				.cumGross(cum == null ? BigDecimal.ZERO : safeDecimal(cum.getCumGr())).cumTax(cumTax)
				.cumSavings(cumSavings).build();
				// message
//				.showTuitionMessage(showTuition).build();
	}

	/*
	 * ═══════════════════════════════════════════════════════════════════ PDF
	 * BUILDER – matches the screenshot layout exactly
	 * ═══════════════════════════════════════════════════════════════════
	 */

	private byte[] buildPdf(PayslipDto dto) throws Exception {

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    Document document = new Document(PageSize.A4, 40, 40, 40, 40);
	    PdfWriter writer = PdfWriter.getInstance(document, baos);
	    document.open();

	    // ── 1. Header ────────────────────────────────────────────────────
	    addHeader(document);

	    // ── 2. Employee info block (boxed) ───────────────────────────────
	    addEmployeeInfo(document, dto);

	    // ── 3. Blank spacer ──────────────────────────────────────────────
	    document.add(new Paragraph(" "));

	    // ── 4. Earnings / Deductions table (boxed) ───────────────────────
	    addEarningsSection(document, dto);

	    // ── 5. Totals section (boxed) ────────────────────────────────────
	    document.add(new Paragraph(" "));
	    addTotalsSection(document, dto);

	    // ── 6. Cumulative section ────────────────────────────────────────
	    document.add(new Paragraph(" "));
	    addCumulativeSection(document, dto);

	    addTuitionMessage(document);
	    
	    onEndPage(writer,document);

	    document.close();
	    return baos.toByteArray();
	}

	/* ── Section helpers ─────────────────────────────────────────────── */

	/**
	 * Centred header with SAIL logo and title inside a bordered box.
	 */
	private void addHeader(Document document) throws Exception {

	    // Outer table with border
	    PdfPTable outerTable = new PdfPTable(1);
	    outerTable.setWidthPercentage(100);
	    outerTable.setSpacingAfter(10);

	    // Inner content table (logo + text)
	    PdfPTable headerTable = new PdfPTable(new float[]{1, 5});
	    headerTable.setWidthPercentage(100);

	    // Logo cell
	    Image logo = Image.getInstance(
	            getClass().getResource("/static/images/sail-logo.png"));
	    logo.scaleToFit(50, 50);

	    PdfPCell logoCell = new PdfPCell(logo, false);
	    logoCell.setBorder(Rectangle.NO_BORDER);
	    logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    logoCell.setPadding(5);
	    headerTable.addCell(logoCell);

	    // Title cell
	    PdfPCell titleCell = new PdfPCell();
	    titleCell.setBorder(Rectangle.NO_BORDER);
	    titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    titleCell.setPadding(5);

	    Paragraph h1 = new Paragraph("JLN Hospital and Research Center, Bhilai", FONT_HEADER_BOLD);
	    h1.setAlignment(Element.ALIGN_CENTER);
	    h1.setIndentationLeft(-50f);
	    titleCell.addElement(h1);

	    Paragraph h2 = new Paragraph("SAIL-Bhilai Steel Plant", FONT_TITLE);
	    h2.setAlignment(Element.ALIGN_CENTER);
	    h2.setIndentationLeft(-50f);
	    titleCell.addElement(h2);

	    Paragraph h3 = new Paragraph("Stipend Statement", FONT_TITLE);
	    h3.setAlignment(Element.ALIGN_CENTER);
	    h3.setIndentationLeft(-50f);
	    titleCell.addElement(h3);

	    headerTable.addCell(titleCell);

	    // Wrap in bordered cell
	    PdfPCell wrapperCell = new PdfPCell(headerTable);
	    wrapperCell.setBorder(Rectangle.BOX);
	    wrapperCell.setBorderWidth(1f);
	    wrapperCell.setPadding(10);
	    outerTable.addCell(wrapperCell);

	    document.add(outerTable);
	}

	/**
	 * Employee information block with all fields in bordered boxes.
	 */
	private void addEmployeeInfo(Document document, PayslipDto dto) throws Exception {

	    PdfPTable t = new PdfPTable(new float[]{2f, 4f, 2f, 4f});
	    t.setWidthPercentage(100);
	    t.setSpacingAfter(5);

	    // Row 1
	    addBoxedPair(t, "Name", dto.getName(), "UID", String.valueOf(dto.getId()));

	    // Row 2
	    addBoxedPair(t, "Post", dto.getCatgDesc(), "Mnth / Yr", dto.getMthYr());

	    // Row 3
	    addBoxedPair(t, "DOJ", dto.getDoj(), "DOS", dto.getDos());

	    // Row 4
	    addBoxedPair(t, "Pan", dto.getPan(), "Bank Cd/Ac No", dto.getBankCd() + " / " + dto.getBankAcno());

	    // Row 5
	    addBoxedPair(t, "Stipend Rate", fmt(dto.getStipendRate()), "Daily Rate", fmt(dto.getDailyRate()));

	    // Row 6
	    addBoxedPair(t, "Attendance", String.valueOf(dto.getDuty()), "", "");

	    document.add(t);
	}

	private void addBoxedPair(PdfPTable t, String l1, String v1, String l2, String v2) {
	    t.addCell(cellBoxedLabel(l1));
	    t.addCell(cellBoxedValue(v1));
	    t.addCell(cellBoxedLabel(l2));
	    t.addCell(cellBoxedValue(v2));
	}

	private PdfPCell cellBoxedLabel(String text) {
	    PdfPCell c = new PdfPCell(new Phrase(text, FONT_HEADER_BOLD));
	    c.setBorder(Rectangle.BOX);
	    c.setBorderWidth(0.5f);
	    c.setPadding(5);
//	    c.setBackgroundColor(new BaseColor(240, 240, 240)); // Light gray background
	    c.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    return c;
	}

	private PdfPCell cellBoxedValue(String text) {
	    PdfPCell c = new PdfPCell(new Phrase(text != null ? text : "", FONT_NORMAL));
	    c.setBorder(Rectangle.BOX);
	    c.setBorderWidth(0.5f);
	    c.setPadding(5);
	    c.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    return c;
	}

	/**
	 * Earnings section with bordered table.
	 */
	private void addEarningsSection(Document document, PayslipDto dto) throws Exception {

	    PdfPTable t = new PdfPTable(3);
	    t.setWidthPercentage(100);
	    t.setWidths(new float[]{1, 1, 1});

	    // Header row
	    addBoxedHeaderCell(t, "Stipend");
	    addBoxedHeaderCell(t, "Adjustment");
	    addBoxedHeaderCell(t, "TDS");

	    // Values row
	    addBoxedDataCell(t, fmt(dto.getStipend()));
	    addBoxedDataCell(t, fmt(dto.getAdj()));
	    addBoxedDataCell(t, fmt(dto.getTds()));

	    document.add(t);
	}

	private void addBoxedHeaderCell(PdfPTable t, String text) {
	    PdfPCell c = new PdfPCell(new Phrase(text, FONT_LABEL));
	    c.setBorder(Rectangle.BOX);
	    c.setBorderWidth(0.5f);
	    c.setPadding(8);
	    c.setHorizontalAlignment(Element.ALIGN_CENTER);
//	    c.setBackgroundColor(new BaseColor(220, 220, 220)); // Darker gray for headers
	    t.addCell(c);
	}

	private void addBoxedDataCell(PdfPTable t, String text) {
	    PdfPCell c = new PdfPCell(new Phrase(text != null ? text : "0", FONT_NORMAL));
	    c.setBorder(Rectangle.BOX);
	    c.setBorderWidth(0.5f);
	    c.setPadding(8);
	    c.setHorizontalAlignment(Element.ALIGN_CENTER);
	    t.addCell(c);
	}

	/**
	 * Totals section (Gross Pay / Net Pay) with bordered boxes.
	 */
	private void addTotalsSection(Document document, PayslipDto dto) throws Exception {

	    PdfPTable t = new PdfPTable(new float[]{3, 1});
	    t.setWidthPercentage(100);

	    // Gross Pay row
	    PdfPCell gpLabel = new PdfPCell(new Phrase("Gross Pay", FONT_LABEL));
	    gpLabel.setBorder(Rectangle.BOX);
	    gpLabel.setBorderWidth(0.5f);
	    gpLabel.setPadding(8);
	    gpLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
//	    gpLabel.setBackgroundColor(new BaseColor(240, 240, 240));
	    t.addCell(gpLabel);

	    PdfPCell gpValue = new PdfPCell(new Phrase(fmt(dto.getGrossPay()), FONT_NORMAL));
	    gpValue.setBorder(Rectangle.BOX);
	    gpValue.setBorderWidth(0.5f);
	    gpValue.setPadding(8);
	    gpValue.setHorizontalAlignment(Element.ALIGN_CENTER);
	    t.addCell(gpValue);

	    // Net Pay row
	    PdfPCell npLabel = new PdfPCell(new Phrase("Net Pay", FONT_HEADER_BOLD));
	    npLabel.setBorder(Rectangle.BOX);
	    npLabel.setBorderWidth(0.5f);
	    npLabel.setPadding(8);
	    npLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
//	    npLabel.setBackgroundColor(new BaseColor(200, 230, 200)); // Light green for emphasis
	    t.addCell(npLabel);

	    PdfPCell npValue = new PdfPCell(new Phrase(fmt(dto.getNetPay()), FONT_HEADER_BOLD));
	    npValue.setBorder(Rectangle.BOX);
	    npValue.setBorderWidth(0.5f);
	    npValue.setPadding(8);
	    npValue.setHorizontalAlignment(Element.ALIGN_CENTER);
//	    npValue.setBackgroundColor(new BaseColor(200, 230, 200)); // Light green for emphasis
	    t.addCell(npValue);

	    document.add(t);
	}

	/**
	 * Cumulative section. Matches lines 27-28 from the SQL:
	 *
	 * Cum.Gross | Cum.Tax | Savings value | value | value
	 */
	private void addCumulativeSection(Document document, PayslipDto dto) throws Exception {

	    log.info("Adding cumulative section for employee id={}, CumGross={}, CumTax={}, CumSavings={}",
	            dto.getId(),
	            dto.getCumGross(),
	            dto.getCumTax(),
	            dto.getCumSavings());

	    // Horizontal line above cumulative section
	    LineSeparator line = new LineSeparator();
	    line.setLineWidth(1f);
	    document.add(new Chunk(line));
	    document.add(new Paragraph(" "));

	    PdfPTable t = noRuleTable(3);

	    // Headers
	    addCellNoRule(t, "Cum.Gross", FONT_LABEL, Element.ALIGN_LEFT);
	    addCellNoRule(t, "Cum.Tax", FONT_LABEL, Element.ALIGN_LEFT);
	    addCellNoRule(t, "Savings", FONT_LABEL, Element.ALIGN_LEFT);

	    // Values
	    addCellNoRule(t, fmt(dto.getCumGross()), FONT_NORMAL, Element.ALIGN_LEFT);
	    addCellNoRule(t, fmt(dto.getCumTax()), FONT_NORMAL, Element.ALIGN_LEFT);
	    addCellNoRule(t, fmt(dto.getCumSavings()), FONT_NORMAL, Element.ALIGN_LEFT);

	    document.add(t);
	}

	/**
	 * Tuition fee message (lines 33-34 in SQL, catg 5 or 6).
	 */
	private void addTuitionMessage(Document document) throws Exception {
		document.add(new Paragraph("\n\n\n\n\n"));
		Paragraph msg = new Paragraph("Message: Your Tuition Fees for the next year is due\n"
				+ "         (after completion of one year from the Date of Joining)", FONT_NORMAL);
		msg.setAlignment(Element.ALIGN_CENTER);
		document.add(msg);
	}

	 

	private PdfPTable noRuleTable(int cols) throws Exception {
		PdfPTable t = new PdfPTable(cols);
		t.setWidthPercentage(100);
		return t;
	}

	/**
	 * Label + value spanning `span` columns (label takes 1 col, value takes span-1)
	 */
	private void addLabelValue(PdfPTable t, String label, String value, int span) {
		PdfPCell labelCell = new PdfPCell(new Phrase(label, FONT_LABEL));
		labelCell.setBorder(Rectangle.NO_BORDER);
		labelCell.setPaddingBottom(3);
		t.addCell(labelCell);

		if (span > 1) {
			PdfPCell valueCell = new PdfPCell(new Phrase(value, FONT_VALUE));
			valueCell.setBorder(Rectangle.NO_BORDER);
			valueCell.setColspan(span - 1);
			t.addCell(valueCell);
		}
	}

	private void addCellNoRule(PdfPTable t, String text, Font font, int align) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(align);
		cell.setPaddingBottom(3);
		t.addCell(cell);
	}
	
	 

	   
	    public void onEndPage(PdfWriter writer, Document document) {

	        PdfPTable footer = new PdfPTable(1);
	        try {
	            footer.setTotalWidth(520);
	            footer.setLockedWidth(true);

	            PdfPCell cell = new PdfPCell(
	                    new Phrase(
	                            "This is a computer-generated payslip and does not require a signature.",
	                            FontFactory.getFont(FontFactory.HELVETICA, 8, Font.ITALIC)));

	            cell.setBorder(Rectangle.TOP);
	            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	            cell.setPaddingTop(5f);
	            cell.setBorderWidthTop(0.5f);

	            footer.addCell(cell);

	            footer.writeSelectedRows(
	                    0,
	                    -1,
	                    document.leftMargin(),
	                    document.bottomMargin() - 10,
	                    writer.getDirectContent());

	        } catch (Exception e) {
	            throw new ExceptionConverter(e);
	        }
	    }
	

	/*
	 * ═══════════════════════════════════════════════════════════════════ UTILITY
	 * HELPERS ═══════════════════════════════════════════════════════════════════
	 */

	private DnbMast getMast(Integer id) {

	    log.info(
	            "Searching DNB master for id={}",
	            id);

	    return dnbMastRepository
	            .findById(id)
	            .orElseThrow(() -> {

	                log.error(
	                        "Employee not found : {}",
	                        id);

	                return new RuntimeException(
	                        "DNB employee not found: "
	                                + id);
	            });
	}

	private DnbPbill getPaybill(Integer yymm, Integer id) {

	    log.info(
	            "Searching paybill for id={} yymm={}",
	            id,
	            yymm);

	    return dnbPaybillRepository
	            .findByIdYymmAndIdId(
	                    yymm,
	                    id)
	            .orElseThrow(() -> {

	                log.error(
	                        "Paybill not found for id={} yymm={}",
	                        id,
	                        yymm);

	                return new RuntimeException(
	                        "Paybill not found for id="
	                                + id
	                                + " yymm="
	                                + yymm);
	            });
	}

	/** Safe null → BigDecimal.ZERO. */
	private BigDecimal safeDecimal(Object val) {
		if (val == null)
			return BigDecimal.ZERO;
		if (val instanceof BigDecimal)
			return (BigDecimal) val;
		if (val instanceof Integer)
			return BigDecimal.valueOf((Integer) val);
		if (val instanceof Long)
			return BigDecimal.valueOf((Long) val);
		if (val instanceof Double)
			return BigDecimal.valueOf((Double) val);
		return new BigDecimal(val.toString());
	}

	private Integer nvlInt(Integer v) {
		return v == null ? 0 : v;
	}

	private String nvl(String v, String def) {
		return v == null || v.isBlank() ? def : v;
	}

	/** Left-pad a string. */
	private String lpad(String s, int len, char pad) {
		if (s == null)
			s = "";
		while (s.length() < len)
			s = pad + s;
		return s;
	}

	/** Format a BigDecimal for display (no decimals). */
	private String fmt(BigDecimal v) {
		return v == null ? "0" : String.valueOf(v.intValue());
	}

	/** Convert YYYYMM integer to "MON-YYYY" string (e.g. 202605 → MAY-2026). */
	private String formatYymm(Integer yymm) {
		if (yymm == null)
			return "";
		String s = String.valueOf(yymm); // "202605"
		int year = Integer.parseInt(s.substring(0, 4));
		int month = Integer.parseInt(s.substring(4, 6));
		String[] months = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
		return months[month - 1] + "-" + year;
	}

	/** Format a java.util.Date or java.sql.Date to "dd-MON-yyyy". */
	private String formatDate(Object date) {
		if (date == null)
			return "";
		try {
			if (date instanceof java.util.Date) {
				return new SimpleDateFormat("dd-MMM-yyyy").format((java.util.Date) date).toUpperCase();
			}
		} catch (Exception e) {
			log.warn("Date format error: {}", e.getMessage());
		}
		return date.toString();
	}
	 

	@Override
	@Transactional(readOnly = true)
	public Page<PayslipSearchDto> searchPayslips(
	        Integer id,
	        String pan,
	        Integer yymm,
	        int page,
	        int size) {

	    if (id == null
	            && (pan == null || pan.isBlank())
	            && yymm == null) {

	        throw new ResourceNotFoundException(
	                "Please enter DNB ID/PAN or select YYMM.");
	    }

	    /*
	     * PAN Search
	     */
	    if (id == null
	            && pan != null
	            && !pan.isBlank()) {

	        DnbMast employee =
	                dnbMastRepository
	                        .findByPanIgnoreCase(
	                                pan.trim())
	                        .orElseThrow(() ->
	                                new ResourceNotFoundException(
	                                        "No employee found for PAN : "
	                                                + pan));

	        id = employee.getId();
	    }

	    /*
	     * Logged-in User Categories
	     */
	    Long loggedInRole =
	            dnbRoleService.getRoleId();

	    List<Integer> allowedCategories =
	            categoryRepository
	                    .findAllowedCategories(
	                            loggedInRole);

	    Page<PayslipSearchDto> pageResult =
	    		dnbPaybillRepository.searchPayslips(
	                    id,
	                    yymm,
	                    allowedCategories,
	                    PageRequest.of(
	                            page,
	                            size));

	    pageResult.getContent()
	            .forEach(dto ->
	                    dto.setMonth(
	                            formatMonth(
	                                    dto.getYymm())));

	    return pageResult;
	}

	private String formatMonth(Integer yymm) {

	    YearMonth ym = YearMonth.parse(
	            String.valueOf(yymm),
	            DateTimeFormatter.ofPattern("yyyyMM"));

	    return ym.format(
	            DateTimeFormatter.ofPattern("MMM-yyyy"));
	}
	
}