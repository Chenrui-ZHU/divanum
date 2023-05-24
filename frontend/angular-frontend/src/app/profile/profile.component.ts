import { Component, OnInit } from '@angular/core';
import { StorageService } from '../_services/storage.service';
import { Observable } from 'rxjs';
import { map, toArray } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  currentUser: any;
  currentUserCodes: Observable<any>;
  fileCodes?: Observable<any>;
  surveyCodes?: Observable<any>;

  updatePWDUrl: any;
  password:any;

  constructor(private storageService: StorageService,
    private http: HttpClient) { }

  ngOnInit(): void {
    this.currentUser = this.storageService.getUser();
    this.currentUserCodes = this.storageService.getCodesByUser(this.currentUser.id);
    this.updatePWDUrl = `http://34.155.164.205/api/auth/password/${this.currentUser.id}/`;

    // Separate the codes into two lists
    this.fileCodes = this.currentUserCodes.pipe(
      map((codes: any[]) => codes.filter(code => !code.includes("QST")))
    );

    this.surveyCodes = this.currentUserCodes.pipe(
      map((codes: any[]) => codes.filter(code => code.includes("QST")))
    );
  }

  modifyPWD():any{
    const pwd = this.password;
    if (pwd) {
      if (pwd.length < 8) {
        alert('Le mot de passe doit contenir au moins 8 caractères.');
        return; // Exit the method if the password is less than 8 characters
      }
    
      const requestBody = { password: pwd };
    
      this.http.post(this.updatePWDUrl+pwd, requestBody).subscribe(
        (response) => {
          // Password modification successful
          alert('Mot de passe modifié avec succès');
          // Perform any necessary actions after successful password modification
        },
        (error) => {
          // Handle error
          console.error('Mot de passe modifié échoué :', error);
          // Perform any necessary error handling actions
        }
      );
    } else {
      // Handle the case where the password is not entered
      alert('Veuillez entrer un mot de passe.');
    }
  }
  
}
