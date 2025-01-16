import { Client, Account } from 'appwrite';
const client = new Client();
client.setProject('673237a1001caeb141b9');
client.setEndpoint('https://cloud.appwrite.io/v1');

const account = new Account(client)
export {account, client};