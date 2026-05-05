import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StatusBadgeComponent } from './status-badge.component';

describe('StatusBadgeComponent', () => {
  let fixture: ComponentFixture<StatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatusBadgeComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(StatusBadgeComponent);
    fixture.componentRef.setInput('status', 'PENDING_VALIDATION');
    fixture.detectChanges();
  });

  it('renders a readable campaign status', () => {
    const text = fixture.nativeElement.textContent as string;
    expect(text).toContain('PENDING VALIDATION');
  });
});
