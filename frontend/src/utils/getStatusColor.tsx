import { Status } from "../enums/Status";


export function getStatusColor(status: Status): string {
    switch (status) {
      case Status.Solved:
        return "text-statusSolved";    
      case Status.Attempted:
        return "text-statusAttempted";
      case Status.Unsolved:
      default:
        return "text-statusUnsolved";
    }
  }