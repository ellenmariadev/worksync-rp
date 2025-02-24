import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  constructor() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  async onSubmit() {
  if (this.loginForm.valid) {
    const { email, password } = this.loginForm.value;
    console.log('Form values:', { email, password });  
    try {
      const success = await this.authService.login(email, password);
      if (success) {
        this.router.navigate(['/home']);
      } else {
        this.errorMessage = 'Falha no login. Por favor, tente novamente.';
      }
    } catch (error: any) {
      this.errorMessage = 'Erro de login ' + error.message;
    }
  } else {
    console.log('Formulário inválido');
  }
}
}