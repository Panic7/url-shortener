import { ErrorHandler, Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({ providedIn: 'root' })
export class GlobalErrorHandler implements ErrorHandler {
  private readonly snackBar = inject(MatSnackBar);

  handleError(error: any): void {
    console.error('Error:', error);

    this.snackBar.open('Something went wrong', 'Close', {
      duration: 3000,
      panelClass: ['snackbar-error'],
    });
  }
}
