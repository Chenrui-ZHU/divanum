import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CaregiverModeratorComponent } from './caregiver-moderator.component';

describe('CaregiverModeratorComponent', () => {
  let component: CaregiverModeratorComponent;
  let fixture: ComponentFixture<CaregiverModeratorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CaregiverModeratorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CaregiverModeratorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
