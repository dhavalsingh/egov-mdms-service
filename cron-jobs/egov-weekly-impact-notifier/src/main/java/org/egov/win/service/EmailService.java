package org.egov.win.service;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.egov.win.model.Body;
import org.egov.win.model.Email;
import org.egov.win.model.PGR;
import org.egov.win.model.PT;
import org.egov.win.model.StateWide;
import org.egov.win.model.TL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {
	
	@Autowired
	private CronService service;
	
	public String formatEmail(Email email) {
        Template t = service.getVelocityTemplate();
        VelocityContext context = new VelocityContext();
        buildEmailBody(email.getBody(), context);
        StringWriter writer = new StringWriter(10000);
        t.merge(context, writer);

        return writer.toString();
	}

	
	private void buildEmailBody(Body body, VelocityContext context) {
		enrichHeaderData(body.getHeader(), context);
		if(null != body.getStateWide()) 
			enrichStateWideData(body.getStateWide(), context);
		if(null != body.getPgr())
			enrichPGRData(body.getPgr(), context);
		if(null != body.getPt())
			enrichPTData(body.getPt(), context);
		if(null != body.getTl())
			enrichTLData(body.getTl(), context);
	}
	
	private void enrichHeaderData(List<Map<String, Object>> header, VelocityContext context) {
		fillData(header, context);
	}
	
	private void enrichStateWideData(StateWide stateWide, VelocityContext context) {
		fillData(stateWide.getNoOfCitizensResgistered(), context);
		fillData(stateWide.getRevenueCollected(), context);
		fillData(stateWide.getServicesApplied(), context);
		fillData(stateWide.getUlbCovered(), context);
	}
	
	private void enrichPGRData(PGR pgr, VelocityContext context) {
		fillData(pgr.getRedressal(), context);
		fillData(pgr.getTotalComplaints(), context);
		fillData(pgr.getUlbCovered(), context);
		fillData(pgr.getChannelBreakup().getIvr(), context);
		fillData(pgr.getChannelBreakup().getMobileApp(), context);
		fillData(pgr.getChannelBreakup().getWebApp(), context);
	}
	
	private void enrichPTData(PT pt, VelocityContext context) {
		fillData(pt.getNoOfProperties(), context);
		fillData(pt.getRevenueCollected(), context);
		fillData(pt.getUlbCovered(), context);
	}
	
	private void enrichTLData(TL tl, VelocityContext context) {
		fillData(tl.getLicenseIssued(), context);
		fillData(tl.getUlbCovered(), context);
	}
		
	private void fillData(List<Map<String, Object>> dataFromQuery, VelocityContext context) {
		dataFromQuery.forEach(record -> {
			for(String key: record.keySet()) {
				context.put(key, record.get(key));
			}
		});
	}

}
