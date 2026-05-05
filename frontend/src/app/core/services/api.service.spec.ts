import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ApiService } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ApiService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(ApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads campaigns from the admin API', () => {
    service.listCampaigns().subscribe((campaigns) => {
      expect(campaigns.length).toBe(1);
      expect(campaigns[0].status).toBe('DRAFT');
    });

    const request = http.expectOne('/api/admin/campaigns');
    expect(request.request.method).toBe('GET');
    request.flush([
      {
        id: '00000000-0000-0000-0000-000000000001',
        name: 'Campagne locale',
        status: 'DRAFT',
        internalDomainAllowlist: 'example.internal',
        validationRequired: true
      }
    ]);
  });

  it('records only allowed public tracking events', () => {
    service.recordPublicEvent({ token: 'token', eventType: 'TRAINING_VIEWED' }).subscribe((response) => {
      expect(response.status).toBe('RECORDED');
    });

    const request = http.expectOne('/api/public/tracking/events');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ token: 'token', eventType: 'TRAINING_VIEWED' });
    request.flush({ status: 'RECORDED' });
  });
});
