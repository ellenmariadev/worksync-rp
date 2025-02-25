import { TestBed } from '@angular/core/testing';
import { HttpEvent, HttpHandler, HttpRequest } from '@angular/common/http';
import { AuthInterceptor } from './auth.interceptor';
import { Observable, of } from 'rxjs';

describe('AuthInterceptor', () => {
  let interceptor: AuthInterceptor;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [interceptor]
    });

    interceptor = TestBed.inject(authInterceptor)!;
  });

  it('should add an Authorization header', () => {
    const token = 'test-token';
    localStorage.setItem('token', token);

    const req = new HttpRequest('GET', '/test');
    const next: HttpHandler = {
      handle: (request: HttpRequest<any>): Observable<HttpEvent<any>> => {
        expect(request.headers.has('Authorization')).toBeTruthy();
        expect(request.headers.get('Authorization')).toBe(`Bearer ${token}`);
        return of({} as HttpEvent<any>);
      }
    };

    interceptor.intercept(req, next).subscribe();
  });

  it('should not add an Authorization header if token is not present', () => {
    localStorage.removeItem('token');

    const req = new HttpRequest('GET', '/test');
    const next: HttpHandler = {
      handle: (request: HttpRequest<any>): Observable<HttpEvent<any>> => {
        expect(request.headers.has('Authorization')).toBeFalsy();
        return of({} as HttpEvent<any>);
      }
    };

    interceptor.intercept(req, next).subscribe();
  });
});