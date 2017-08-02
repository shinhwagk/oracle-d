import { BrowserModule } from '@angular/platform-browser';
import { ErrorHandler, NgModule } from '@angular/core';
import { HttpModule } from '@angular/http';
import { IonicApp, IonicErrorHandler, IonicModule } from 'ionic-angular';

import { MyApp } from './app.component';
import { HomePage } from '../pages/home/home';
import { ListPage } from '../pages/list/list';
import { AwrPage } from '../pages/awr/awr';
import { AwrSinglePage } from '../pages/awr/single/single';

import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';
import { TopSqlPage } from '../pages/topsql/topsql';
import { KeyPerformance } from '../pages/keyperformance/keyperformance';

@NgModule({
  declarations: [
    MyApp,
    HomePage,
    ListPage,
    AwrPage,
    AwrSinglePage,
    TopSqlPage,
    KeyPerformance
  ],
  imports: [
    BrowserModule,
    HttpModule,
    IonicModule.forRoot(MyApp),
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    HomePage,
    ListPage,
    AwrPage,
    AwrSinglePage,
    TopSqlPage,
    KeyPerformance
  ],
  providers: [
    StatusBar,
    SplashScreen,
    { provide: ErrorHandler, useClass: IonicErrorHandler }
  ]
})
export class AppModule { }
