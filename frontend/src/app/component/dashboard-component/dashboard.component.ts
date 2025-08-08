import { Component, OnInit, signal } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { ShortLinkService } from '../../service/short-link.service';
import { ShortLinkResponse } from '../../model/short-link-response';
import { inject } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-dashboard-component',
  imports: [
    MatCardModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent implements OnInit {
  private shortLinkService = inject(ShortLinkService);

  protected readonly shortLinks = signal<ShortLinkResponse[]>([]);
  protected readonly isLoading = signal(true);
  protected readonly displayedColumns = ['shortUrl', 'clickCount'];

  protected readonly totalElements = signal(0);
  protected readonly pageSize = signal(5);
  protected readonly pageIndex = signal(0);

  ngOnInit(): void {
    this.loadShortLinks();
  }

  protected handlePageEvent(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadShortLinks();
  }

  private loadShortLinks(): void {
    this.isLoading.set(true);
    this.shortLinkService
      .getMyShortLinks(this.pageIndex(), this.pageSize())
      .subscribe({
        next: (response) => {
          this.shortLinks.set(response.content as ShortLinkResponse[]);
          this.totalElements.set(response.totalElements);
          this.isLoading.set(false);
        },
        error: () => {
          this.isLoading.set(false);
        },
      });
  }
}
