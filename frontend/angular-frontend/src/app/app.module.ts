import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { SurveyModule } from "survey-angular-ui";
import { SurveyCreatorModule } from 'survey-creator-angular';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { BoardUserComponent } from './board-user/board-user.component';
import { SurveyComponent } from './survey/survey.component';

import { httpInterceptorProviders } from './_helpers/http.interceptor';
import { FileUploadComponent } from './components/file-upload/file-upload.component';
import { SurveyCreatorComponent } from './survey-creator/survey-creator.component';
import { CaregiverModeratorComponent } from './caregiver-moderator/caregiver-moderator.component';
import { RegisterAdminComponent } from './register_admin/register_admin.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    ProfileComponent,
    BoardAdminComponent,
    BoardUserComponent,
    FileUploadComponent,
    SurveyComponent,
    SurveyCreatorComponent,
    CaregiverModeratorComponent,
    RegisterAdminComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    SurveyModule,
    SurveyCreatorModule,
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }
