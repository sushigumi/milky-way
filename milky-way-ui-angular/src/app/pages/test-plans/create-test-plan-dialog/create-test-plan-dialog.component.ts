import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { NgClass, NgForOf, NgIf } from '@angular/common';

const COMPLETED_STEP_CLASSES: string =
  'group border-indigo-600 hover:border-indigo-800 hover:cursor-pointer';
const COMPLETED_SPAN_STEP_CLASSES: string =
  'text-indigo-600 group-hover:text-indigo-800';
const CURRENT_STEP_CLASSES: string = 'border-indigo-600';
const CURRENT_SPAN_STEP_CLASSES: string = 'text-indigo-600';
const PENDING_STEP_CLASSES: string = 'group border-gray-200';
const PENDING_SPAN_STEP_CLASSES: string = 'text-gray-500';

@Component({
  selector: 'app-create-test-plan-dialog',
  standalone: true,
  imports: [NgIf, NgClass, NgForOf],
  templateUrl: './create-test-plan-dialog.component.html',
  styleUrl: './create-test-plan-dialog.component.css',
  animations: [
    trigger('overlayOpenClose', [
      state(
        'closed',
        style({
          opacity: 0,
        }),
      ),
      state(
        'open',
        style({
          opacity: 100,
        }),
      ),
      transition('closed => open', [animate('300ms ease-out')]),
      transition('open => closed', [animate('200ms ease-in')]),
    ]),
    trigger('modalOpenClose', [
      state(
        'closed',
        style({
          opacity: 0,
          transform: 'translateY(1rem)',
        }),
      ),
      state(
        'open',
        style({
          opacity: 100,
          transform: 'translateY(0)',
        }),
      ),
      transition('closed => open', [animate('300ms ease-out')]),
      transition('open => closed', [animate('200ms ease-in')]),
    ]),
  ],
})
export class CreateTestPlanDialogComponent {
  @Input() isOpen!: boolean;
  @Output() modalClosed = new EventEmitter<void>();

  currentStep: number = 0;
  steps = [
    {
      display: 'Select Template',
      classes: CURRENT_STEP_CLASSES,
      spanClasses: CURRENT_SPAN_STEP_CLASSES,
    },
    {
      display: 'Queue Tests',
      classes: PENDING_STEP_CLASSES,
      spanClasses: PENDING_SPAN_STEP_CLASSES,
    },
    {
      display: 'Finalize',
      classes: PENDING_STEP_CLASSES,
      spanClasses: PENDING_SPAN_STEP_CLASSES,
    },
  ];

  get finalStep() {
    return this.steps.length - 1;
  }

  onCloseModal() {
    this.modalClosed.emit();
    this.resetProgressState();
  }

  onClickStep(step: number) {
    // Don't allow a click if it is greater or equal to the current step.
    if (step >= this.currentStep) {
      return;
    }

    this.currentStep = step;
    this.setProgressState();
  }

  onClickNextOrFinishButton() {
    this.currentStep++;
    // If there are still more steps, then we update the state.
    if (this.currentStep < this.steps.length) {
      const prevStep = this.currentStep - 1;

      this.steps[prevStep].classes = COMPLETED_STEP_CLASSES;
      this.steps[prevStep].spanClasses = COMPLETED_SPAN_STEP_CLASSES;
      this.steps[this.currentStep].classes = CURRENT_STEP_CLASSES;
      this.steps[this.currentStep].spanClasses = CURRENT_SPAN_STEP_CLASSES;
    }
    // Else, send a request to the server and reset the state.
    else {
      this.resetProgressState();
    }
  }

  private resetProgressState() {
    this.currentStep = 0;
    this.setProgressState();
  }

  private setProgressState() {
    for (let i = 0; i < this.currentStep; ++i) {
      this.steps[i].classes = COMPLETED_STEP_CLASSES;
      this.steps[i].spanClasses = COMPLETED_SPAN_STEP_CLASSES;
    }

    this.steps[this.currentStep].classes = CURRENT_STEP_CLASSES;
    this.steps[this.currentStep].spanClasses = CURRENT_SPAN_STEP_CLASSES;

    for (let i = this.currentStep + 1; i < this.steps.length; ++i) {
      this.steps[i].classes = PENDING_STEP_CLASSES;
      this.steps[i].spanClasses = PENDING_SPAN_STEP_CLASSES;
    }
  }
}
