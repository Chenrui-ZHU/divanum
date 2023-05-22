import { TestBed } from '@angular/core/testing';

import { CaregiverModeratorService } from './caregiver-moderator.service';

describe('CaregiverModeratorService', () => {
  let service: CaregiverModeratorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CaregiverModeratorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
