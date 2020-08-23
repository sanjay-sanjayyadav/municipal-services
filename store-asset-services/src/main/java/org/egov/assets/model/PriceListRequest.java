package org.egov.assets.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contract class for web request. Array of PriceList items  are used in case of create or update
 */
public class PriceListRequest   {
  @JsonProperty("RequestInfo")
  private RequestInfo requestInfo = null;

  @JsonProperty("priceLists")
  private List<PriceList> priceLists = new ArrayList<>();

  public PriceListRequest requestInfo(RequestInfo requestInfo) {
    this.requestInfo = requestInfo;
    return this;
  }

   /**
   * Get requestInfo
   * @return requestInfo
  **/
  @NotNull

  @Valid

  public RequestInfo getRequestInfo() {
    return requestInfo;
  }

  public void setRequestInfo(RequestInfo requestInfo) {
    this.requestInfo = requestInfo;
  }

  public PriceListRequest priceLists(List<PriceList> priceLists) {
    this.priceLists = priceLists;
    return this;
  }

  public PriceListRequest addPriceListsItem(PriceList priceListsItem) {
    this.priceLists.add(priceListsItem);
    return this;
  }

   /**
   * Used for search result and create only
   * @return priceLists
  **/
  
  @NotNull

  @Valid

  public List<PriceList> getPriceLists() {
    return priceLists;
  }

  public void setPriceLists(List<PriceList> priceLists) {
    this.priceLists = priceLists;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PriceListRequest priceListRequest = (PriceListRequest) o;
    return Objects.equals(this.requestInfo, priceListRequest.requestInfo) &&
        Objects.equals(this.priceLists, priceListRequest.priceLists);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestInfo, priceLists);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PriceListRequest {\n");
    
    sb.append("    requestInfo: ").append(toIndentedString(requestInfo)).append("\n");
    sb.append("    priceLists: ").append(toIndentedString(priceLists)).append("\n");
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

