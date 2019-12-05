package org.egov.waterConnection.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.egov.waterConnection.model.Property;
import org.egov.waterConnection.model.PropertyCriteria;
import org.egov.waterConnection.model.PropertyRequest;
import org.egov.waterConnection.model.PropertyResponse;
import org.egov.waterConnection.model.RequestInfoWrapper;
import org.egov.waterConnection.model.WaterConnectionRequest;
import org.egov.waterConnection.model.SearchCriteria;
import org.egov.waterConnection.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WaterServicesUtil {

	private ServiceRequestRepository serviceRequestRepository;

	@Value("${egov.property.service.host}")
	private String propertyHost;

	@Value("${egov.property.createendpoint}")
	private String createPropertyEndPoint;

	@Value("${egov.property.searchendpoint}")
	private String searchPropertyEndPoint;


	@Autowired
	public WaterServicesUtil(ServiceRequestRepository serviceRequestRepository) {
		this.serviceRequestRepository = serviceRequestRepository;

	}

	/**
	 * 
	 * @param waterConnectionRequest
	 *            WaterConnectionRequest containing property
	 * @return List of Property
	 */
	public List<Property> propertySearch(WaterConnectionRequest waterConnectionRequest) {
		Set<String> propertyIds = new HashSet<>();
		List<Property> propertyList = new ArrayList<>();
		PropertyCriteria propertyCriteria = new PropertyCriteria();
		HashMap<String, Object> propertyRequestObj = new HashMap<>();
		propertyIds.add(waterConnectionRequest.getWaterConnection().getProperty().getPropertyId());
		propertyCriteria.setPropertyIds(propertyIds);
		propertyRequestObj.put("RequestInfoWrapper",
				getPropertyRequestInfoWrapperSearch(new RequestInfoWrapper(), waterConnectionRequest.getRequestInfo()));
		propertyRequestObj.put("PropertyCriteria", propertyCriteria);
		Object result = serviceRequestRepository.fetchResult(getPropertyURL(), propertyRequestObj);
		propertyList = getPropertyDetails(result);
		if (propertyList == null || propertyList.isEmpty()) {
			throw new CustomException("INCORRECT PROPERTY ID", "WATER CONNECTION CAN NOT BE CREATE");
		}
		return propertyList;
	}

	/**
	 * 
	 * @param waterConnectionRequest
	 * @return Created property list
	 */
	public List<Property> createPropertyRequest(WaterConnectionRequest waterConnectionRequest) {
		List<Property> propertyList = new ArrayList<>();
		propertyList.add(waterConnectionRequest.getWaterConnection().getProperty());
		PropertyRequest propertyReq = getPropertyRequest(waterConnectionRequest.getRequestInfo(), waterConnectionRequest.getWaterConnection().getProperty());
		Object result = serviceRequestRepository.fetchResult(getPropertyURL(), propertyReq);
		return getPropertyDetails(result);
	}

	/**
	 * 
	 * @param waterConnectionSearchCriteria
	 *            WaterConnectionSearchCriteria containing search criteria on
	 *            water connection
	 * @param requestInfo
	 * @return List of property matching on given criteria
	 */
	public List<Property> propertySearchOnCriteria(SearchCriteria waterConnectionSearchCriteria,
			RequestInfo requestInfo) {
		if ((waterConnectionSearchCriteria.getTenantId() == null
				|| waterConnectionSearchCriteria.getTenantId().isEmpty())) {
			throw new CustomException("INVALID SEARCH", "TENANT ID NOT PRESENT");
		}
		if ((waterConnectionSearchCriteria.getMobileNumber() == null
				|| waterConnectionSearchCriteria.getMobileNumber().isEmpty())) {
			return Collections.emptyList();
		}
		HashMap<String, Object> propertyRequestObj = new HashMap<>();
		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		PropertyCriteria propertyCriteria = new PropertyCriteria();
		if (waterConnectionSearchCriteria.getTenantId() != null
				&& !waterConnectionSearchCriteria.getTenantId().isEmpty()) {
			propertyCriteria.setTenantId(waterConnectionSearchCriteria.getTenantId());
		}
		if (waterConnectionSearchCriteria.getMobileNumber() != null
				&& !waterConnectionSearchCriteria.getMobileNumber().isEmpty()) {
			propertyCriteria.setMobileNumber(waterConnectionSearchCriteria.getMobileNumber());
		}
		Object result = serviceRequestRepository.fetchResult(
				getPropURL(waterConnectionSearchCriteria.getTenantId(),
						waterConnectionSearchCriteria.getMobileNumber()),
				RequestInfoWrapper.builder().requestInfo(requestInfo).build());
		return getPropertyDetails(result);
	}

	private RequestInfoWrapper getPropertyRequestInfoWrapperSearch(RequestInfoWrapper requestInfoWrapper,
			RequestInfo requestInfo) {
		RequestInfoWrapper requestInfoWrapper_new = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		return requestInfoWrapper_new;
	}

	
	/**
	 * 
	 * @param result
	 *            Response object from property service call
	 * @return List of property
	 */
	private List<Property> getPropertyDetails(Object result) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			PropertyResponse propertyResponse = mapper.convertValue(result, PropertyResponse.class);
			return propertyResponse.getProperties();
		} catch (Exception ex) {
			throw new CustomException("PARSING ERROR", "The property json cannot be parsed");
		}
	}

	private PropertyRequest getPropertyRequest(RequestInfo requestInfo, Property propertyList) {
		PropertyRequest propertyReq = PropertyRequest.builder().requestInfo(requestInfo).property(propertyList)
				.build();
		return propertyReq;
	}

	public StringBuilder getPropertyCreateURL() {
		return new StringBuilder().append(propertyHost).append(createPropertyEndPoint);
	}

	public StringBuilder getPropertyURL() {
		return new StringBuilder().append(propertyHost).append(searchPropertyEndPoint);
	}

	public MdmsCriteriaReq prepareMdMsRequest(String tenantId, String moduleName, List<String> names, String filter,
			RequestInfo requestInfo) {
		List<MasterDetail> masterDetails = new ArrayList<>();
		names.forEach(name -> {
			masterDetails.add(MasterDetail.builder().name(name).filter(filter).build());
		});
		ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(moduleName).masterDetails(masterDetails).build();
		List<ModuleDetail> moduleDetails = new ArrayList<>();
		moduleDetails.add(moduleDetail);
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
		return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
	}
	
	public MdmsCriteriaReq prepareMdMsRequest(String tenantId, String moduleName, List<String> names, String filter,MdmsCriteriaReq mdmsCriteriaReq,RequestInfo requestInfo) {
		List<MasterDetail> masterDetails = new ArrayList<>();
		names.forEach(name -> {
			masterDetails.add(MasterDetail.builder().name(name).filter(filter).build());
		});
		ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(moduleName).masterDetails(masterDetails).build();
		List<ModuleDetail> moduleDetails = new ArrayList<>();
		moduleDetails.add(moduleDetail);
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
		return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
	}
	
	 /**
	  * 
	  * @return
	  */
	private String getPropertySearchURL() {
		StringBuilder url = new StringBuilder(getPropertyURL());
		url.append("?");
		url.append("tenantId=");
		url.append("{1}");
		url.append("&");
		url.append("mobileNumber=");
		url.append("{2}");
		return url.toString();
	}
	private StringBuilder getPropURL(String tenantId, String mobileNumber){
		 String url = getPropertySearchURL();
		 url = url.replace("{1}",tenantId).replace("{2}",mobileNumber);
		 return new StringBuilder(url);
	}
}
