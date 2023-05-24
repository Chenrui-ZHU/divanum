import { Component, OnInit, ViewChild } from '@angular/core';
import { UserService } from '../_services/user.service';
import { AuthService } from '../_services/auth.service';
import { StorageService } from '../_services/storage.service';
import { Observable, Subscription } from 'rxjs';
import { EventBusService } from '../_shared/event-bus.service';
import { FileUploadService } from '../_services/file-upload.service';
import { FileUploadComponent } from '../components/file-upload/file-upload.component';

@Component({
  selector: 'app-board-user',
  templateUrl: './board-user.component.html',
  styleUrls: ['./board-user.component.css']
})
export class BoardUserComponent implements OnInit {
  @ViewChild(FileUploadComponent)fileComponent: FileUploadComponent;
  
  content?: string;
  eventBusSub?: Subscription;
  id: any;

  constructor(
    private userService: UserService,
    private storageService: StorageService,
    private authService: AuthService,
    private eventBusService: EventBusService,
    private uploadService:  FileUploadService,
  ) { }

  myFiles?: Observable<any>;

  ngOnInit(): void {
    this.id = this.storageService.getUser().id;
    this.myFiles = this.uploadService.getFilesOfUser(this.id); 
  }

}


  // logout(): void {
  //   this.authService.logout().subscribe({
  //     next: res => {
  //       console.log(res);
  //       this.storageService.clean();

  //       window.location.reload();
  //     },
  //     error: err => {
  //       console.log(err);
  //     }
  //   });
  // }

  // this.userService.getUserBoard().subscribe({
  //   next: data => {
  //     this.content = data;
  //   },
  //   error: err => {
  //     if (err.error) {
  //       try {
  //         const res = JSON.parse(err.error);
  //         this.content = res.message;
  //       } catch {
  //         this.content = `Error with status: ${err.status} - ${err.statusText}`;
  //       }
  //     } else {
  //       this.content = `Error with status: ${err.status}`;
  //     }
  //   }
  // });