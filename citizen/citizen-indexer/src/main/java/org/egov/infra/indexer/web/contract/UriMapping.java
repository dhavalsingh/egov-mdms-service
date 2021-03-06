package org.egov.infra.indexer.web.contract;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UriMapping {

	  @JsonProperty("path")
	  private String path;
	  
	  @JsonProperty("apiRequest")
	  private Object request;
	  
	  @JsonProperty("queryParam")
	  private String queryParam;
	  
	  @JsonProperty("pathParam")
	  private String pathParam;
	  
	  @JsonProperty("uriResponseMapping")
	  private List<FieldMapping> uriResponseMapping;
}
