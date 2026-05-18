import { http } from './http'
import type { ClaimStatusResponse, PolicySummaryResponse } from '@/types/insurance'

export async function fetchPolicySummary(policyNo: string) {
  const { data } = await http.get<PolicySummaryResponse>(`/api/policies/${policyNo}/summary`)
  return data
}

export async function fetchClaimStatus(claimNo: string) {
  const { data } = await http.get<ClaimStatusResponse>(`/api/claims/${claimNo}/status`)
  return data
}