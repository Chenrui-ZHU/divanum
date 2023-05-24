import { Component, NgModule, OnInit } from '@angular/core';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FileUploadService } from 'src/app/_services/file-upload.service';
import { StorageService } from 'src/app/_services/storage.service';
import { SurveyService } from "../../_services/survey.service";
import { SurveyPDF, IDocOptions } from "survey-pdf";
import { Model, StylesManager } from "survey-core";
import "survey-core/survey.i18n";

const exportToPdfOptions: IDocOptions = {
  fontSize: 12
};

const savePdf = function (surveyData: any, surveyJson: any) {
  const surveyPdf = new SurveyPDF(surveyJson, exportToPdfOptions);
  surveyPdf.data = surveyData;
  surveyPdf.raw().then((pdfData) => {
    const formData = new FormData();
    const surveyName = new Date().getTime() + "_results.pdf";
    formData.append("results", new Blob([pdfData], { type: "application/pdf" }), surveyName);

    // Send the PDF data to the server to save it to the database
    fetch(`http://34.155.164.205/api/auth/survey/results`, {
      method: "POST",
      body: formData
    })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Failed to save PDF file to database");
      }
      console.log("PDF file saved to database successfully");
    })
    .catch((error) => {
      console.error(error);
    });
  });
};

StylesManager.applyTheme("modern");

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css'],
})

export class FileUploadComponent implements OnInit {
  
  selectedFiles?: FileList;
  currentFile?: File;
  progress = 0;
  message = '';
  count = false;
  toUpload = false;
  toSurvey = false;
  fileInfos?: Observable<any>;
  form: any = {
    code: null,
  };
  allCodes?: Observable<any>;

  title = "My First Survey";
  surveyModel: Model;

  constructor(
    private uploadService: FileUploadService,
    private storageService: StorageService,
    private surveyService: SurveyService
  ) {}
  
  ngOnInit(): void {
    this.fileInfos = this.uploadService.getFiles();
    
  }

  file_survey(): void{
    const{ code } = this.form;
    if(code.includes("QST")){
      this.allCodes = this.storageService.getAllCodes();
      this.allCodes.subscribe((data) => {
        const codeExistsAndNotUsed1 = data.some((c: { info: any; used: boolean; }) => {
          return c.info === code && !c.used;
        });
      
        if (codeExistsAndNotUsed1) {
          this.toSurvey = !this.toSurvey;
          this.surveyService.getSurveyByCode(code).subscribe((surveyJson) => {
            const survey = new Model(surveyJson);
            survey.locale = "fr";
            survey.onComplete.add((sender) => this.logSurveyResults(sender, surveyJson));
            this.surveyModel = survey;
          })
        } else {
          alert("Le code est utilisé ou n'existe pas.");
        }
      });
    }
    else{
      this.allCodes = this.storageService.getAllCodes();
      // let codeExistsAndNotUsed = false;
      this.allCodes.subscribe((data) => {
        const codeExistsAndNotUsed = data.some((c: { info: any; used: boolean; }) => {
          console.log(c.used);
          return c.info === code && !c.used;
        });
      
        if (codeExistsAndNotUsed) {
          this.toUpload = !this.toUpload;
          this.message = '';
          this.progress = 0;
          this.currentFile = null as unknown as File;
        } else {
          alert("Le code est utilisé ou n'existe pas.");
        }
      });
    }
  }

  logSurveyResults(sender: { data: any; }, surveyJson: any) {
    console.log(sender.data);
    savePdf(sender.data, surveyJson);
  }

  selectFile(event: any): void {
    this.selectedFiles = event.target.files;
  }

  upload(): void {
    this.progress = 0;

    const{ code } = this.form;
    console.log(code);

    if (this.selectedFiles) {
      const file: File | null = this.selectedFiles.item(0);

      if (file) {
        this.currentFile = file;

        this.uploadService.upload(this.currentFile,code).subscribe({
          next: (event: any) => {
            if (event.type === HttpEventType.UploadProgress) {
              this.progress = Math.round((100 * event.loaded) / event.total);
            } else if (event instanceof HttpResponse) {
              this.message = event.body.message;
              this.fileInfos = this.uploadService.getFiles();
            }
          },
          error: (err: any) => {
            console.log(err);
            this.progress = 0;

            if (err.error && err.error.message) {
              this.message = err.error.message;
            } else {
              this.message = 'Impossible de télécharger le fichier!';
            }

            this.currentFile = undefined;
          },
        });
      }

      this.selectedFiles = undefined;
    }
  }

  uploadById(id: null): void {
    this.progress = 0;

    if (this.selectedFiles) {
      const file: File | null = this.selectedFiles.item(0);

      if (file) {
        this.currentFile = file;

        this.uploadService.uploadById(this.currentFile, id).subscribe({
          next: (event: any) => {
            if (event.type === HttpEventType.UploadProgress) {
              this.progress = Math.round((100 * event.loaded) / event.total);
            } else if (event instanceof HttpResponse) {
              this.message = event.body.message;
              this.fileInfos = this.uploadService.getFiles();
            }
          },
          error: (err: any) => {
            console.log(err);
            this.progress = 0;

            if (err.error && err.error.message) {
              this.message = err.error.message;
            } else {
              this.message = 'Impossible de télécharger le fichier!';
            }

            this.currentFile = undefined;
          },
        });
      }

      this.selectedFiles = undefined;
    }
  }

  
}
