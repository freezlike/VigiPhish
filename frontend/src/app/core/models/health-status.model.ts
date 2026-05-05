export interface HealthStatus {
  status: 'UP' | 'DOWN' | string;
  application: string;
  checkedAt: string;
}
