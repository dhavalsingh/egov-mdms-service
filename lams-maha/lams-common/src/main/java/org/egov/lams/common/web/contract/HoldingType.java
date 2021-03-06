package org.egov.lams.common.web.contract;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * An object which holds the Holding Type Master info
 */
@ApiModel(description = "An object which holds the Holding Type Master info")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-11-09T07:10:49.937Z")

public class HoldingType   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("tenantId")
  private String tenantId = null;

  @JsonProperty("code")
  private String code = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("isActive")
  private Boolean isActive = null;

  public HoldingType id(String id) {
    this.id = id;
    return this;
  }

   /**
   * Unique Identifier of the HoldingType.
   * @return id
  **/
  @ApiModelProperty(value = "Unique Identifier of the HoldingType.")

 @Size(min=1,max=256)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public HoldingType tenantId(String tenantId) {
    this.tenantId = tenantId;
    return this;
  }

   /**
   * tenant id of the HoldingType
   * @return tenantId
  **/
  @ApiModelProperty(required = true, value = "tenant id of the HoldingType")
  @NotNull

 @Size(min=2,max=128)
  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public HoldingType code(String code) {
    this.code = code;
    return this;
  }

   /**
   * code of the HoldingType
   * @return code
  **/
  @ApiModelProperty(required = true, value = "code of the HoldingType")
  @NotNull

 @Size(min=1,max=64)
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public HoldingType name(String name) {
    this.name = name;
    return this;
  }

   /**
   * type of holding
   * @return name
  **/
  @ApiModelProperty(required = true, value = "type of holding")
  @NotNull

 @Size(min=1,max=128)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public HoldingType isActive(Boolean isActive) {
    this.isActive = isActive;
    return this;
  }

   /**
   * is HoldingType is active.
   * @return isActive
  **/
  @ApiModelProperty(required = true, value = "is HoldingType is active.")
  @NotNull


  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HoldingType holdingType = (HoldingType) o;
    return Objects.equals(this.id, holdingType.id) &&
        Objects.equals(this.tenantId, holdingType.tenantId) &&
        Objects.equals(this.code, holdingType.code) &&
        Objects.equals(this.name, holdingType.name) &&
        Objects.equals(this.isActive, holdingType.isActive);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, tenantId, code, name, isActive);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HoldingType {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    tenantId: ").append(toIndentedString(tenantId)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    isActive: ").append(toIndentedString(isActive)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

