import {HttpStatus} from './http-status';

export interface CustomHttpResponse {
  httpStatus: HttpStatus;
  message: string;
}
