package com.anypresence.play.lifecyclehooks

import play.api.Application
import play.api.Logger._
import play.api.Plugin

class LifecycleHooksPlugin(app: Application) extends Plugin {
  
  override def enabled: Boolean = {
    true
  }
  
  override def onStart(): Unit = {
    debug("LifecycleHooksPlugin starting")
  }
  
  override def onStop(): Unit = {
    debug("LifecycleHooksPlugin stopping")
  }
  
}