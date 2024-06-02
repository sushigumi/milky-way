import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NgClass, NgForOf, NgIf } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, NgForOf, NgClass, NgIf],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  items = [
    { name: 'Home', href: '/' },
    { name: 'Test Plans', href: '/test-plans' },
  ];

  selectedIndex: number = 0;

  selectedNavItemClasses: Record<string, boolean> = {
    'bg-gray-900': true,
    'text-white': true,
  };

  defaultNavItemClasses: Record<string, boolean> = {
    'text-gray-300': true,
    'hover:bg-gray-700': true,
    'hover:text-white': true,
  };

  isMobileMenuOpen: boolean = false;

  onClickNavItem(index: number) {
    this.selectedIndex = index;
  }

  isSelected(index: number) {
    return this.selectedIndex == index;
  }

  onClickMobileMenuButton() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }
}
