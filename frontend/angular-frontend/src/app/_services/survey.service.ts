import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SurveyService {

  constructor(private http: HttpClient) {}

  public getSurvey(survey_id: any): Observable<any> {
    return this.http.get(`http://34.155.164.205/api/auth/survey/${survey_id}`);
  }

  public getLatestSurvey(): any {
    return this.http.get('http://34.155.164.205/api/auth/survey');
  }

  public getSurveyByCode(surveycode: any): Observable<any> {
    return this.http.get(`http://34.155.164.205/api/auth/survey/code/${surveycode}`);
  }

  public getAllSurveyResults(): void{
    
  }

}
