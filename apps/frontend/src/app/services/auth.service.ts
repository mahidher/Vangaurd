import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from '@angular/router';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService implements CanActivate {

  constructor(private userService: UserService, private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const user = this.userService.getLoggedInUserValue();
    if (!user || (route.routeConfig?.path === 'admin' && !user.isAdmin)) {
      this.userService.logout();
      this.router.navigate(['/login']);
      return false;
    }
    return true;
  }
}
