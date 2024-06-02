import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateTestPlanDialogComponent } from './create-test-plan-dialog.component';

describe('CreateTestPlanDialogComponent', () => {
  let component: CreateTestPlanDialogComponent;
  let fixture: ComponentFixture<CreateTestPlanDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateTestPlanDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateTestPlanDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
