
import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ShortUrl } from '../model/short-url';
import { ShortLinkRequest } from '../model/short-link-request';
import { ShortLinkResponse } from '../model/short-link-response';
import { PageResponse } from '../model/page-response';

@Injectable({
  providedIn: 'root',
})
export class ShortLinkService {
  private http = inject(HttpClient);

  shortenUrl(shortLinkRequest: ShortLinkRequest) {
    return this.http.post<ShortUrl>(`/api/shorten`, shortLinkRequest);
  }

  redirectToOriginalUrl(shortCode: string) {
    return this.http.get<void>(`/api/shorten/${shortCode}`);
  }

  getMyShortLinks(page = 0, size = 20) {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<ShortLinkResponse>>(`/api/short-links`, {
      params,
    });
  }
}
