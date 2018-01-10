package org.egov.inv.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONArray;
import org.egov.common.Constants;
import org.egov.common.DomainService;
import org.egov.common.MdmsRepository;
import org.egov.common.Pagination;
import org.egov.common.exception.CustomBindException;
import org.egov.common.exception.ErrorCode;
import org.egov.common.exception.InvalidDataException;
import org.egov.inv.model.*;
import org.egov.inv.persistence.entity.MaterialEntity;
import org.egov.inv.persistence.repository.MaterialJdbcRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.util.StringUtils.isEmpty;

@Service
public class MaterialService extends DomainService {

    @Autowired
    private MaterialJdbcRepository materialJdbcRepository;

    @Autowired
    private MaterialStoreMappingService materialStoreMappingService;

    @Value("${inv.materials.save.topic}")
    private String saveTopic;

    @Value("${inv.materials.save.key}")
    private String saveKey;

    @Value("${inv.materials.update.topic}")
    private String updateTopic;

    @Value("${inv.materials.update.key}")
    private String updateKey;

    @Autowired
    private MdmsRepository mdmsRepository;

    public static final String SEQ_SERIAL_NO = "seq_material_code_serial_no";

    public MaterialResponse save(MaterialRequest materialRequest, String tenantId) {

        try {
            List<MaterialStoreMapping> materialStoreMappings = new ArrayList<>();

            validate(materialRequest.getMaterials(), Constants.ACTION_CREATE);

            List<String> materialIdList = materialJdbcRepository.getSequence(Material.class.getSimpleName(), materialRequest.getMaterials().size());

            for (int i = 0; i <= materialIdList.size() - 1; i++) {
                materialRequest.getMaterials().get(i)
                        .setId(materialIdList.get(i).toString());
                if (isEmpty(materialRequest.getMaterials().get(i).getTenantId())) {
                    materialRequest.getMaterials().get(i).setTenantId(tenantId);
                }
                materialRequest.getMaterials().get(i).setAuditDetails(mapAuditDetails(materialRequest.getRequestInfo()));
                materialStoreMappings = buildMaterialStoreMapping(materialRequest.getMaterials().get(i).getCode(), materialRequest.getMaterials().get(i).getStoreMapping());
            }

            kafkaQue.send(saveTopic, saveKey, materialRequest);

            materialStoreMappingService.create(buildMaterialStoreRequest(materialRequest.getRequestInfo(), materialStoreMappings), tenantId);

            MaterialResponse response = MaterialResponse.builder()
                    .materials(materialRequest.getMaterials())
                    .responseInfo(getResponseInfo(materialRequest.getRequestInfo()))
                    .build();

            return response;
        } catch (CustomBindException e) {
            throw e;
        }
    }

    public MaterialResponse update(MaterialRequest materialRequest, String tenantId) {
        try {
            List<MaterialStoreMapping> materialStoreMappings = new ArrayList<>();

            validate(materialRequest.getMaterials(), Constants.ACTION_UPDATE);
            for (Material material : materialRequest.getMaterials()) {
                if (isEmpty(material.getTenantId())) {
                    material.setTenantId(tenantId);
                }
                material.setAuditDetails(mapAuditDetails(materialRequest.getRequestInfo()));
                materialStoreMappings = buildMaterialStoreMapping(material.getCode(), material.getStoreMapping());
            }

            kafkaQue.send(saveTopic, saveKey, materialRequest);

            materialStoreMappingService.update(buildMaterialStoreRequest(materialRequest.getRequestInfo(), materialStoreMappings), tenantId);

            MaterialResponse response = new MaterialResponse();
            response.setResponseInfo(getResponseInfo(materialRequest.getRequestInfo()));
            response.setMaterials(materialRequest.getMaterials());
            return response;

        } catch (CustomBindException e) {
            throw e;
        }
    }

    /*Provides only the mapped materials if we are searching by tenantid, 
    on passing the code we get the material objects from mdms and database*/
    public MaterialResponse search(MaterialSearchRequest materialSearchRequest, org.egov.common.contract.request.RequestInfo requestInfo) {

        Map<String, Material> materialFromMdms = getMaterialFromMdms(materialSearchRequest.getTenantId());

        Pagination<Material> materialFromDb = materialJdbcRepository.search(materialSearchRequest);

        if (materialFromDb.getPagedData().size() > 0){
        
        for (Material material : materialFromDb.getPagedData()) {

            Material mdmsMaterial = materialFromMdms.get(material.getCode());

            if (null != mdmsMaterial) {
                prepareMaterial(material, mdmsMaterial);

                List<StoreMapping> storeMappings = new ArrayList<>();

                MaterialStoreMappingSearch materialStoreMappingSearch = MaterialStoreMappingSearch.builder()
                        .material(material.getCode())
                        .tenantId(material.getTenantId())
                        .build();

                List<MaterialStoreMapping> materialStoreMappings = materialStoreMappingService.search(materialStoreMappingSearch, requestInfo).getMaterialStoreMappings();

                materialStoreMappings.forEach(materialStoreMapping -> {
                            StoreMapping storeMapping = StoreMapping.builder()
                                    .id(materialStoreMapping.getId())
                                    .chartofAccount(materialStoreMapping.getChartofAccount())
                                    .active(materialStoreMapping.getActive())
                                    .store(materialStoreMapping.getStore())
                                    .auditDetails(materialStoreMapping.getAuditDetails())
                                    .build();
                            storeMappings.add(storeMapping);
                        }
                );
                material.setStoreMapping(storeMappings);
            }
        }
        MaterialResponse response = new MaterialResponse();
        response.setMaterials(materialFromDb.getPagedData().size() > 0 ? materialFromDb.getPagedData() : Collections.emptyList());
        return response;
        }
        else
        {
        	List<Material> list = new ArrayList<>();
            Material mdmsMaterial = materialFromMdms.get(materialSearchRequest.getCode());
            if(null != mdmsMaterial){
            mdmsMaterial.setStoreMapping(Collections.EMPTY_LIST);
            list.add(mdmsMaterial);
            }else
            {
            	throw new CustomException("Material" , "Material Not Found with code " + materialSearchRequest.getCode());
            }
        	 MaterialResponse response = new MaterialResponse();
             response.setMaterials(list);
             return response;
        }
       
    }

    public Material fetchMaterial(final String tenantId, final String code,
                                  final RequestInfo requestInfo) {

        JSONArray responseJSONArray;
        final ObjectMapper mapper = new ObjectMapper();

        responseJSONArray = mdmsRepository.getByCriteria(tenantId, "inventory",
                "Material", "code", code, requestInfo);

        if (responseJSONArray != null && responseJSONArray.size() > 0)
            return mapper.convertValue(responseJSONArray.get(0), Material.class);
        else
            throw new CustomException("Material", "Given Material is invalid: " + code);

    }

    private void validate(List<Material> materials, String method) {

        try {
            switch (method) {

                case Constants.ACTION_CREATE: {
                    if (materials == null) {
                        throw new InvalidDataException("materialstore", ErrorCode.NOT_NULL.getCode(), null);
                    } else {
                        materials.forEach(material -> {
                            minmaxvalidate(material);
//                            validateMaterial(material.getCode(),material.getTenantId());
                        });
                    }
                }

                break;

                case Constants.ACTION_UPDATE: {
                    if (materials == null) {
                        throw new InvalidDataException("materialstore", ErrorCode.NOT_NULL.getCode(), null);
                    } else {
                        materials.forEach(material -> {
                            minmaxvalidate(material);
                        });
                    }
                }

                break;
            }
        } catch (IllegalArgumentException e) {

        }

    }

    private List<MaterialStoreMapping> buildMaterialStoreMapping(String materialCode, List<StoreMapping> storeMappings) {
        List<MaterialStoreMapping> materialStoreMappings = new ArrayList<>();

        storeMappings.stream().forEach(
                storeMapping -> {
                    MaterialStoreMapping materialStoreMapping = MaterialStoreMapping.builder()
                            .id(storeMapping.getId())
                            .material(buildMaterial(materialCode))
                            .store(storeMapping.getStore())
                            .chartofAccount(storeMapping.getChartofAccount())
                            .active(storeMapping.getActive())
                            .auditDetails(storeMapping.getAuditDetails())
                            .delete(storeMapping.getDelete())
                            .build();

                    materialStoreMappings.add(materialStoreMapping);
                }
        );
        return materialStoreMappings;
    }

    private Material buildMaterial(String materialCode) {
        return Material
                .builder()
                .code(materialCode)
                .build();
    }

    private MaterialStoreMappingRequest buildMaterialStoreRequest(RequestInfo requestInfo,
                                                                  List<MaterialStoreMapping> materialStoreMappings) {
        return MaterialStoreMappingRequest.builder()
                .requestInfo(requestInfo)
                .materialStoreMappings(materialStoreMappings)
                .build();
    }


    private void minmaxvalidate(Material material) {
        if (material.getMaxQuantity().compareTo(material.getMinQuantity()) < 0) {
            throw new CustomException("inv.0013", "maximum quantity should be greater than minimum quantity");
        }
    }


    private Map<String, Material> getMaterialFromMdms(String tenantId) {

        List<Object> objectList = mdmsRepository.fetchObjectList(tenantId, "inventory", "Material", null, null, Material.class);
        Map<String, Material> materialHashMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        if (objectList != null && objectList.size() > 0) {
            for (Object object : objectList) {
                Material material = mapper.convertValue(object, Material.class);
                materialHashMap.put(material.getCode(), material);
            }
        }
        return materialHashMap;
    }

    private void prepareMaterial(Material material, Material mdmsMaterial) {
        material.baseUom(mdmsMaterial.getBaseUom())
                .purchaseUom(mdmsMaterial.getPurchaseUom())
                .stockingUom(mdmsMaterial.getStockingUom())
                .name(mdmsMaterial.getName())
                .oldCode(mdmsMaterial.getOldCode())
                .assetCategory(mdmsMaterial.getAssetCategory())
                .description(mdmsMaterial.getDescription())
                .expenseAccount(mdmsMaterial.getExpenseAccount())
                .inActiveDate(mdmsMaterial.getInActiveDate())
                .inventoryType(mdmsMaterial.getInventoryType())
                .lotControl(mdmsMaterial.getLotControl())
                .manufacturePartNo(mdmsMaterial.getManufacturePartNo())
                .materialClass(mdmsMaterial.getMaterialClass())
                .materialType(mdmsMaterial.getMaterialType())
                .scrapable(mdmsMaterial.getScrapable())
                .serialNumber(mdmsMaterial.getSerialNumber())
                .shelfLifeControl(mdmsMaterial.getShelfLifeControl())
                .model(mdmsMaterial.getModel())
                .status(mdmsMaterial.getStatus())
                .techincalSpecs(mdmsMaterial.getTechincalSpecs())
                .termsOfDelivery(mdmsMaterial.getTermsOfDelivery())
                .tenantId(mdmsMaterial.getTenantId());
    }

 /*   private void validateMaterial(String code, String tenantId) {
        Material material = (Material) mdmsRepository.fetchObject(tenantId, "inventory", "Material", "code", code, Material.class);
        if (null == material) {
            throw new CustomException("inv.0014", "Material not found with Code" + code);

        }
    }
*/

}
