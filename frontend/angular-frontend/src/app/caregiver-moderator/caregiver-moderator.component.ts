import { Component, OnInit } from '@angular/core';
import { Observable, Subscription, map } from 'rxjs';
import { CaregiverModeratorService } from 'src/app/_services/caregiver-moderator.service';
import { FileUploadService } from '../_services/file-upload.service';
import { StorageService } from '../_services/storage.service';

@Component({
  selector: 'app-caregiver-moderator',
  templateUrl: './caregiver-moderator.component.html',
  styleUrls: ['./caregiver-moderator.component.css']
})
export class CaregiverModeratorComponent implements OnInit{
  
  users?: Observable<any>;
  fileInfos?: Observable<any>;
  codes?: Observable<any>;
  fileCodes?: Observable<any>;
  surveyCodes?: Observable<any>;

  downloadFilesByUserUrl: any;

  constructor(
    private caregiverModeratorService: CaregiverModeratorService,
    private uploadService: FileUploadService,
    public storageService: StorageService
  ) {}

  ngOnInit(): void {
    this.users = this.caregiverModeratorService.getAllUsers();
  }

  selectedUser: any;

  onSelect(user: any): void {
    this.selectedUser = user;
    this.fileInfos = this.uploadService.getFilesOfUser(this.selectedUser.id);
    this.codes = this.storageService.getCodesByUser(this.selectedUser.id);
    // Separate the codes into two lists
    this.fileCodes = this.codes.pipe(
      map((codes: any[]) => codes.filter(code => !code.includes("SURVEY")))
    );

    this.surveyCodes = this.codes.pipe(
      map((codes: any[]) => codes.filter(code => code.includes("SURVEY")))
    );
    this.downloadFilesByUserUrl = `http://34.155.164.205/api/auth/download/${this.selectedUser.id}/files`;
  }

}
