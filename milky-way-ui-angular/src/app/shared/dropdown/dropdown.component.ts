import { Component } from '@angular/core';
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-dropdown',
  standalone: true,
  imports: [NgIf],
  templateUrl: './dropdown.component.html',
  styleUrl: './dropdown.component.css',
  animations: [
    trigger('openClose', [
      state(
        'closed',
        style({
          opacity: 0,
          scale: 0.95,
        }),
      ),
      state(
        'open',
        style({
          opacity: 100,
          scale: 1,
        }),
      ),
      transition('open => closed', [animate('100ms ease-out')]),
      transition('closed => open', [animate('75ms ease-in')]),
    ]),
  ],
})
export class DropdownComponent {
  isOpen = false;

  toggle() {
    this.isOpen = !this.isOpen;
  }
}
