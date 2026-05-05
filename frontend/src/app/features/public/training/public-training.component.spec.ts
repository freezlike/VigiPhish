import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { PublicTrainingComponent } from './public-training.component';
import { ApiService } from '../../../core/services/api.service';

describe('PublicTrainingComponent', () => {
  let fixture: ComponentFixture<PublicTrainingComponent>;
  let api: jasmine.SpyObj<ApiService>;

  beforeEach(async () => {
    api = jasmine.createSpyObj<ApiService>('ApiService', ['recordPublicEvent', 'getPublicAwarenessPage']);
    api.getPublicAwarenessPage.and.returnValue(of({
      title: 'Page test',
      educationalMessage: 'Message interne de sensibilisation',
      content: 'Aucun secret ne doit être saisi.'
    }));
    api.recordPublicEvent.and.returnValue(of({ status: 'RECORDED' }));

    await TestBed.configureTestingModule({
      imports: [PublicTrainingComponent],
      providers: [
        { provide: ApiService, useValue: api },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: new Map([['token', 'abc']]) } } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PublicTrainingComponent);
    fixture.detectChanges();
  });

  it('records a training view without submitting sensitive data', () => {
    expect(api.getPublicAwarenessPage).toHaveBeenCalledWith('abc');
    expect(api.recordPublicEvent).toHaveBeenCalledWith({ token: 'abc', eventType: 'LINK_CLICKED' });
    expect(api.recordPublicEvent).toHaveBeenCalledWith({ token: 'abc', eventType: 'TRAINING_VIEWED' });
    expect(fixture.nativeElement.textContent).toContain('Page test');
    expect(fixture.nativeElement.textContent).toContain('Aucun secret');
  });
});
