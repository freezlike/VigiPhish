import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PageHeaderComponent } from './page-header.component';

describe('PageHeaderComponent', () => {
  let fixture: ComponentFixture<PageHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PageHeaderComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(PageHeaderComponent);
    fixture.componentRef.setInput('title', 'Campagnes');
    fixture.componentRef.setInput('description', 'Pilotage interne');
    fixture.detectChanges();
  });

  it('renders title and description', () => {
    const text = fixture.nativeElement.textContent as string;
    expect(text).toContain('Campagnes');
    expect(text).toContain('Pilotage interne');
  });
});
