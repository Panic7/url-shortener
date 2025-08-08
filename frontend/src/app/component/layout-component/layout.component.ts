import { Component, signal, inject, OnInit } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import {
  RouterOutlet,
  Router,
  RouterLink,
} from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-layout-component',
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss',
})
export class LayoutComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly title = signal('ShortenIt');
  protected readonly isAuthenticated = this.authService.isAuthenticated;

  ngOnInit(): void {
    this.authService.fetchCurrentUser().subscribe();
  }
  protected readonly user = this.authService.currentUser;

  protected onLogin(): void {
    this.router.navigate(['/login']);
  }

  protected onRegister(): void {
    this.router.navigate(['/register']);
  }

  protected onLogout(): void {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/']);
    });
  }
}
