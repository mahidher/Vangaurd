export interface Transaction {
  transactionId?: string;
  amount: number;
  fromUserName: string;
  toUserName: string;
  timestamp?: Date;
  description?: string;
}