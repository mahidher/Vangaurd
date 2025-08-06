import { Transaction } from "./transactions.model";

export interface User {
    userId?: string;
    userName: string;
    isAdmin?: boolean;
    balance?: number;
}

export interface UserTransactionSummary {
    userName: string;
    balance: number;
    createdAt: Date;
    transactions: Transaction[]
}