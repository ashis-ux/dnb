package com.bsp.dnb.serviceImpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsp.dnb.dto.PayBillDto;
import com.bsp.dnb.service.PayBillService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PayBillServiceImpl implements PayBillService {

	private static final Logger log = LoggerFactory.getLogger(PayBillServiceImpl.class);

	@Autowired
	private DataSource dataSource;

	@Override
	public PayBillDto process(Integer userInput) {

		log.info("Calling DEMO procedure for input : {}", userInput);

		PayBillDto dto = new PayBillDto();

		try (Connection conn = dataSource.getConnection();

				CallableStatement cs = conn.prepareCall("{call DEMO(?,?,?,?)}")) {

			cs.setInt(1, userInput);

			cs.registerOutParameter(2, Types.VARCHAR);

			cs.registerOutParameter(3, Types.NUMERIC);

			cs.registerOutParameter(4, Types.VARCHAR);

			cs.execute();

			dto.setStatusMsg(cs.getString(2));

			dto.setStatusCode(cs.getInt(3));

			dto.setException(cs.getString(4));

			log.info("Procedure completed. StatusCode : {}, StatusMessage : {}", dto.getStatusCode(),
					dto.getStatusMsg());

		} catch (Exception ex) {

			log.error("Error while executing DEMO procedure", ex);

			dto.setStatusCode(0);

			dto.setStatusMsg("FAILED");

			dto.setException(ex.getMessage());
		}

		return dto;
	}

	 

}
