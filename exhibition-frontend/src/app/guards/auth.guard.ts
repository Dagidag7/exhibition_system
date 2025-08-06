import { Injectable } from '@angular/core';
import { CanActivate, CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate, CanActivateChild {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return this.checkAuth(route);
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return this.checkAuth(childRoute);
  }

  private checkAuth(route: ActivatedRouteSnapshot): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: route.url.join('/') } });
      return false;
    }

    const requiredRole = route.data['role'];
    if (requiredRole && !this.authService.hasRole(requiredRole)) {
      // Redirect based on user's actual role
      const currentUser = this.authService.getCurrentUser();
      if (currentUser) {
        if (currentUser.role === 'admin') {
          this.router.navigate(['/admin']);
        } else if (currentUser.role === 'exhibitor') {
          this.router.navigate(['/exhibitor']);
        } else {
          // For attendees, redirect to landing page
          this.router.navigate(['/']);
        }
      } else {
        this.router.navigate(['/']);
      }
      return false;
    }

    return true;
  }
} 