import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { FileUploadService } from 'src/app/_services/file-upload.service';
import { CaregiverModeratorService } from 'src/app/_services/caregiver-moderator.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-board-admin',
  templateUrl: './board-admin.component.html',
  styleUrls: ['./board-admin.component.css']
})
export class BoardAdminComponent implements OnInit {
  content?: string;
  fileInfos?: Observable<any>;
  onShow?: any;
  users?: Observable<any>;
  down = 0;
  up = 5;
  pageSize = 5;
  currentPage = 1;

  downloadUrl = 'http://34.155.164.205/api/auth/download/files';
  downloadFilesByUserUrl: any;
  downloadResultsUrl = 'http://34.155.164.205/api/auth/survey/survey_result';

  constructor(
    private uploadService: FileUploadService,
    private caregiverModeratorService: CaregiverModeratorService
  ) { }

  ngOnInit(): void {
    this.users = this.caregiverModeratorService.getAllUsers();
    this.onShow = false;
  }

  selectedUser: any;

  onSelect(user: any): void {
    this.selectedUser = user;
    this.fileInfos = this.uploadService.getFilesOfUser(this.selectedUser.id);
    this.downloadFilesByUserUrl = `http://34.155.164.205/api/auth/download/${this.selectedUser.id}/files`;
  }

  showNextPage():void{
    this.down += this.pageSize;
    this.up += this.pageSize;
    this.currentPage++;
  }

  showLastPage():void{
    this.down -= this.pageSize;
    this.up -= this.pageSize;
    this.currentPage--;
  }

}
