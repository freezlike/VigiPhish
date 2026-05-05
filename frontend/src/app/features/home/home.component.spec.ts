import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { HomeComponent } from './home.component';
import { HealthService } from '../../core/services/health.service';

describe('HomeComponent', () => {
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [
        provideHttpClient(),
        {
          provide: HealthService,
          useValue: {
            getHealth: () =>
              of({
                status: 'UP',
                application: 'dssi-phishing-awareness',
                checkedAt: '2026-05-05T10:15:30Z'
              })
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    fixture.detectChanges();
  });

  it('shows the application title and backend status', () => {
    const text = fixture.nativeElement.textContent as string;

    expect(text).toContain('DSSI Phishing Awareness');
    expect(text).toContain('UP');
  });
});
