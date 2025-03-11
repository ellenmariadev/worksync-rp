// Definindo o tipo para um projeto
export interface ProjectDTO {
  id: number;
  title: string;
  description: string;
  participantIds: number[];
  taskIds: number[];
}
