package com.bsp.dnb.serviceImpl;

import com.bsp.dnb.dto.PayslipDto;
import com.bsp.dnb.dto.PayslipSearchDto;
import com.bsp.dnb.entity.*;
import com.bsp.dnb.exception.NoResourceFoundException;
import com.bsp.dnb.repo.*;
import com.bsp.dnb.service.DnbRoleService;
import com.bsp.dnb.service.PayslipService;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;

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
	            throw new NoResourceFoundException("DNBMAST record not found for id=" + id);
	        }

	        DnbAtt att = dnbAttRepository.findAttendance(id, yymm)
	                .orElseThrow(() ->
	                        new NoResourceFoundException("Attendance not found for id=" + id + ", yymm=" + yymm));

	        DnbPbill paybill = getPaybill(yymm, id);
	        if (paybill == null) {
	            throw new NoResourceFoundException("Paybill not found for id=" + id + ", yymm=" + yymm);
	        }

	        DnbCum cum = dnbCumRepository.findFirstByIdAndYymm(id, yymm);
	        if (cum == null) {
	            throw new NoResourceFoundException("DnbCum not found for id=" + id + ", yymm=" + yymm);
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

		// ── 2. Horizontal rule ───────────────────────────────────────────
		addHRule(document);

		// ── 3. Employee info block ───────────────────────────────────────
		addEmployeeInfo(document, dto);

		// ── 4. Blank spacer ──────────────────────────────────────────────
		document.add(new Paragraph(" "));

		// ── 5. Earnings / Deductions table header ────────────────────────
		addEarningsSection(document, dto);

		// ── 6. Horizontal rule ───────────────────────────────────────────
		document.add(new Paragraph(" "));
		addHRule(document);
		document.add(new Paragraph(" "));

		// ── 7. Cumulative section ────────────────────────────────────────
		addCumulativeSection(document, dto);
		
		addTuitionMessage(document);

//		// ── 8. Optional tuition message ──────────────────────────────────
//		if (dto.isShowTuitionMessage()) {
//			addTuitionMessage(document);
//		}

		document.close();
		return baos.toByteArray();
	}

	/* ── Section helpers ─────────────────────────────────────────────── */

	/**
	 * Centred three-line header with SAIL logo placeholder. Screenshot shows: JLN
	 * Hospital... / SAIL-Bhilai Steel Plant / Stipend Statement
	 */
	private void addHeader(Document document) throws Exception {

		// Logo + text side by side
		PdfPTable headerTable = new PdfPTable(new float[] { 1, 5 });
		headerTable.setWidthPercentage(85);
		headerTable.setSpacingAfter(4);

		// Logo cell (placeholder box)
		Image logo = Image.getInstance(
		        getClass().getResource("/static/images/sail-logo.png"));

		logo.scaleToFit(50, 50);

		PdfPCell logoCell = new PdfPCell(logo, false);
		logoCell.setBorder(Rectangle.NO_BORDER);
		logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		headerTable.addCell(logoCell);

		// Title cell
		PdfPCell titleCell = new PdfPCell();
		titleCell.setBorder(Rectangle.NO_BORDER);
		titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);

		Paragraph h1 = new Paragraph("JLN Hospital and Research Center, Bhilai", FONT_HEADER_BOLD);
		h1.setAlignment(Element.ALIGN_CENTER);
		titleCell.addElement(h1);

		Paragraph h2 = new Paragraph("SAIL-Bhilai Steel Plant", FONT_TITLE);
		h2.setAlignment(Element.ALIGN_CENTER);
		titleCell.addElement(h2);

		Paragraph h3 = new Paragraph("Stipend Statement", FONT_TITLE);
		h3.setAlignment(Element.ALIGN_CENTER);
		titleCell.addElement(h3);

		headerTable.addCell(titleCell);
		document.add(headerTable);
	}

	/**
	 * Full-width horizontal rule (mimics the ___ line in the SQL output).
	 */
	private void addHRule(Document document) throws Exception {
		PdfPTable rule = new PdfPTable(1);
		rule.setWidthPercentage(100);
		PdfPCell cell = new PdfPCell(new Phrase(""));
		cell.setBorderWidthTop(0.5f);
		cell.setBorderWidthBottom(0);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setPaddingBottom(2);
		rule.addCell(cell);
		document.add(rule);
	}

	/**
	 * Employee information block. Matches lines 10, 10a, 10b, 10c from the SQL.
	 *
	 * line10 : Name | UID | Mth/Yr line10a : Post | DOJ | DOS line10b : PAN | Bank
	 * Cd/Acno line10c : Stipend Rate | Daily Rate | Attendance
	 */
		private void addEmployeeInfo(Document document, PayslipDto dto) throws Exception {

		    PdfPTable t = new PdfPTable(new float[]{2f, 4f, 2f, 4f});
		    t.setWidthPercentage(100);

		    // Row 1
		    addPair(t,
		            "Name :", dto.getName(),
		            "UID :", String.valueOf(dto.getId()));

		    // Row 2
		    addPair(t,
		            "Post :", dto.getCatgDesc(),
		            "Mnth / Yr :", dto.getMthYr());

		    // Row 3
		    addPair(t,
		            "DOJ :", dto.getDoj(),
		            "DOS :", dto.getDos());

		    // Row 4
		    addPair(t,
		            "Pan :", dto.getPan(),
		            "Bank Cd/Ac No :", dto.getBankCd() + " / " + dto.getBankAcno());

		    // Row 5
		    addPair(t,
		            "Stipend Rate :", fmt(dto.getStipendRate()),
		            "Daily Rate :", fmt(dto.getDailyRate()));

		    // Row 6 (last single values → span properly)
		    t.addCell(cellLabel("Attendance :"));
		    t.addCell(cellValue(String.valueOf(dto.getDuty())));
		    t.addCell(cellEmpty());
		    t.addCell(cellEmpty());

		    document.add(t);
		}
		
		private void addPair(PdfPTable t, String l1, String v1, String l2, String v2) {

		    t.addCell(cellLabel(l1));
		    t.addCell(cellValue(v1));

		    t.addCell(cellLabel(l2));
		    t.addCell(cellValue(v2));
		}
		
		private PdfPCell cellLabel(String text) {
		    PdfPCell c = new PdfPCell(new Phrase(text, FONT_HEADER_BOLD));
		    c.setBorder(Rectangle.NO_BORDER);
		    c.setNoWrap(true);
		    return c;
		}

		private PdfPCell cellValue(String text) {
		    PdfPCell c = new PdfPCell(new Phrase(text, FONT_NORMAL));
		    c.setBorder(Rectangle.NO_BORDER);
		    return c;
		}

		private PdfPCell cellEmpty() {
		    PdfPCell c = new PdfPCell(new Phrase(""));
		    c.setBorder(Rectangle.NO_BORDER);
		    return c;
		}

	/**
	 * Earnings section. Matches lines 13-20 from the SQL:
	 *
	 * Stipend | Adjustment | TDS Gross Pay value | value | value value Net Pay
	 * value
	 */
		private void addEarningsSection(Document document, PayslipDto dto) throws Exception {

			PdfPTable t = new PdfPTable(new float[] { 2, 4, 2, 4 });
		    t.setWidthPercentage(100);

		    // Header
		    addCellNoRule(t, "Stipend", FONT_LABEL, Element.ALIGN_LEFT);
		    addCellNoRule(t, "Adjustment", FONT_LABEL, Element.ALIGN_LEFT);
		    addCellNoRule(t, "TDS", FONT_LABEL, Element.ALIGN_LEFT);
		    addCellNoRule(t, "", FONT_LABEL, Element.ALIGN_RIGHT);

		    // Values
		    addCellNoRule(t, fmt(dto.getStipend()), FONT_NORMAL, Element.ALIGN_LEFT);
		    addCellNoRule(t, fmt(dto.getAdj()), FONT_NORMAL, Element.ALIGN_LEFT);
		    addCellNoRule(t, fmt(dto.getTds()), FONT_NORMAL, Element.ALIGN_LEFT);
		    addCellNoRule(t, "", FONT_VALUE, Element.ALIGN_RIGHT);

		    // GAP (IMPORTANT: use spacing instead of fake rows)
		    t.setSpacingAfter(10f);

		    // Gross Pay row (clean alignment)
		    PdfPCell gpLabel = new PdfPCell(new Phrase("Gross Pay", FONT_LABEL));
		    gpLabel.setBorder(Rectangle.NO_BORDER);
		    gpLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
		    gpLabel.setColspan(3);

		    PdfPCell gpValue = new PdfPCell(new Phrase(fmt(dto.getGrossPay()), FONT_NORMAL));
		    gpValue.setBorder(Rectangle.NO_BORDER);
		    gpValue.setHorizontalAlignment(Element.ALIGN_LEFT);

		    t.addCell(gpLabel);
		    t.addCell(gpValue);

		    // Net Pay row
		    PdfPCell npLabel = new PdfPCell(new Phrase("Net Pay", FONT_LABEL));
		    npLabel.setBorder(Rectangle.NO_BORDER);
		    npLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
		    npLabel.setColspan(3);

		    PdfPCell npValue = new PdfPCell(new Phrase(fmt(dto.getNetPay()), FONT_NORMAL));
		    npValue.setBorder(Rectangle.NO_BORDER);
		    npValue.setHorizontalAlignment(Element.ALIGN_LEFT);

		    t.addCell(npLabel);
		    t.addCell(npValue);

		    document.add(t);
		}

	/**
	 * Cumulative section. Matches lines 27-28 from the SQL:
	 *
	 * Cum.Gross | Cum.Tax | Savings value | value | value
	 */
	private void addCumulativeSection(Document document, PayslipDto dto) throws Exception {

		PdfPTable t = noRuleTable(3);

		// Headers (line27)
		addCellNoRule(t, "Cum.Gross", FONT_LABEL, Element.ALIGN_LEFT);
		addCellNoRule(t, "Cum.Tax", FONT_LABEL, Element.ALIGN_LEFT);
		addCellNoRule(t, "Savings", FONT_LABEL, Element.ALIGN_LEFT);

		// Values (line28)
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

	        throw new NoResourceFoundException(
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
	                                new NoResourceFoundException(
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