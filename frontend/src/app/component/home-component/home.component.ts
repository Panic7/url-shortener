import {
  ChangeDetectionStrategy,
  Component,
  inject,
  signal,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ShortLinkService } from '../../service/short-link.service';
import { ShortLinkRequest } from '../../model/short-link-request';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home-component',
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    ReactiveFormsModule,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent {
  private readonly shortLinkService = inject(ShortLinkService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly fb = inject(FormBuilder);

  protected readonly shortenedUrl = signal('');
  protected readonly isLoading = signal(false);
  protected readonly httpErrorMessage = signal('');

  protected readonly urlForm = this.fb.group({
    url: [
      '',
      {
        nonNullable: true,
        validators: [Validators.required],
      },
    ],
  });

  shortenUrl(): void {
    if (this.urlForm.invalid) {
      return;
    }

    this.httpErrorMessage.set('');
    this.isLoading.set(true);

    const request: ShortLinkRequest = {
      url: this.urlForm.get('url')?.value || '',
    };

    this.urlForm.disable();

    this.shortLinkService.shortenUrl(request).subscribe({
      next: (response) => {
        this.shortenedUrl.set(response.shortenedUrl);
        this.isLoading.set(false);
        this.urlForm.enable();
      },
      error: (error) => {
        this.httpErrorMessage.set(
          error.error?.description || 'Ensure the URL is valid and try again',
        );
        this.isLoading.set(false);
        this.urlForm.enable();
      },
    });
  }

  copyToClipboard(): void {
    navigator.clipboard.writeText(this.shortenedUrl()).then(() => {
      this.snackBar.open('URL copied to clipboard', 'Close', {
        duration: 3000,
      });
    });
  }
}
