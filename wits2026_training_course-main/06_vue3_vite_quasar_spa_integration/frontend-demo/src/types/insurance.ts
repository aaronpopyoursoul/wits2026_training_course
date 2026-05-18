export interface PolicySummaryResponse {
  policyNo: string
  policyHolderName: string
  productCode: string
  policyStatus: string
}

export interface ClaimStatusResponse {
  claimNo: string
  status: string
  assignedAdjuster: string
}