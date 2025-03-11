export type CommentResponse = {
  id: number;
  description: string;
  taskId: number;
  userId: number;
  createdAt: string;
}


export type CommentRequest = {
  description: string;
  taskId: number;
  userId: number;
}
