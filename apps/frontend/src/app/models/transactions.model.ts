export interface TransactionListResponse {
  current_balance: string;
  transaction_history: Transaction[]
}

export interface Transaction {
  transaction_id: string;
  transaction_type: string;
  amount: string;
  username: string;
  timestamp: Date
}