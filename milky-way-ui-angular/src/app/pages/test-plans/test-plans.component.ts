import { Component } from '@angular/core';
import { DropdownComponent } from '../../shared/dropdown/dropdown.component';
import { TestPlan } from './shared/test-plan.model';
import { NgForOf } from '@angular/common';
import { CreateTestPlanDialogComponent } from './create-test-plan-dialog/create-test-plan-dialog.component';

@Component({
  selector: 'app-test-plans',
  standalone: true,
  imports: [DropdownComponent, NgForOf, CreateTestPlanDialogComponent],
  templateUrl: './test-plans.component.html',
  styleUrl: './test-plans.component.css',
})
export class TestPlansComponent {
  private testPlans: TestPlan[] = [];
  visibleTestPlans: TestPlan[] = [
    { id: 'asdf', name: 'asdf', status: 'pending' },
  ];

  isCreateTestPlanModalOpen = false;

  onClosedCreateTestPlanModal() {
    this.isCreateTestPlanModalOpen = false;
  }
}
