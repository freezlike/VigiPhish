import { Component, OnInit, computed, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HealthService } from '../../core/services/health.service';
import { HealthStatus } from '../../core/models/health-status.model';

type HealthViewState = 'loading' | 'up' | 'unavailable';

@Component({
  selector: 'app-home',
  imports: [DatePipe],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  protected readonly health = signal<HealthStatus | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly state = computed<HealthViewState>(() => {
    if (this.error()) {
      return 'unavailable';
    }

    return this.health() ? 'up' : 'loading';
  });

  constructor(private readonly healthService: HealthService) {}

  ngOnInit(): void {
    this.healthService.getHealth().subscribe({
      next: (health) => {
        this.health.set(health);
        this.error.set(null);
      },
      error: () => {
        this.health.set(null);
        this.error.set('Backend unavailable');
      }
    });
  }
}
