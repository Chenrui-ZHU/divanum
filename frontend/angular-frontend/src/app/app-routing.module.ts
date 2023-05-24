import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RegisterComponent } from './register/register.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { BoardUserComponent } from './board-user/board-user.component';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { FileUploadComponent } from './components/file-upload/file-upload.component';
import { SurveyComponent } from './survey/survey.component';
import { SurveyCreatorComponent } from './survey-creator/survey-creator.component';
import { CaregiverModeratorComponent } from './caregiver-moderator/caregiver-moderator.component';
import { RegisterAdminComponent } from './register_admin/register_admin.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'register_admin', component: RegisterAdminComponent},
  { path: 'profile', component: ProfileComponent },
  { path: 'user', component: BoardUserComponent },
  { path: 'upload', component: FileUploadComponent },
  { path: 'survey', component: SurveyComponent},
  { path: 'survey-creator', component: SurveyCreatorComponent},
  { path: 'admin', component: BoardAdminComponent },
  { path: 'caregiver-moderator', component: CaregiverModeratorComponent},
  { path: '', redirectTo: 'home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
