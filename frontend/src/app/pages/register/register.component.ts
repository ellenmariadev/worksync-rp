import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { translateError } from '../../utils/translateErrors';

export function passwordMatcher(
  control: AbstractControl
): ValidationErrors | null {
  const password = control.get('password');
  const confirmPassword = control.get('confirmPassword');

  if (password && confirmPassword && password.value !== confirmPassword.value) {
    return { passwordMismatch: true };
  }

  return null;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage: string = '';
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  constructor() {
    this.registerForm = this.fb.group(
      {
        name: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', Validators.required],
        role: ['USER'],
      },
      {
        validators: passwordMatcher,
      }
    );
  }

  get f() {
    return this.registerForm.controls;
  }

  async onSubmit(): Promise<void> {
    if (this.registerForm.invalid) {
      this.errorMessage = 'Preencha todos os campos corretamente.';
      return;
    }

    const { name, email, password, role } = this.registerForm.value;

    const response = await this.authService.register(
      name,
      email,
      password,
      role
    );

    this.errorMessage =
      'error' in response ? translateError(response.error.message) : '';
    if (!this.errorMessage) {
      alert('Cadastro realizado com sucesso!');
      this.router.navigate(['/login']);
    }
  }
}
