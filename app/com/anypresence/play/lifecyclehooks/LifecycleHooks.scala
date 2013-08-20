package com.anypresence.play.lifecyclehooks

import play.api.Logger._
import scala.collection.mutable.Buffer

object LifecycleHooks {
  
  type BeforeHookFunction[InstanceType] = (InstanceType) => Any
  type AfterHookFunction[InstanceType, ReturnType] = (InstanceType, ReturnType) => Any
  
  private class Hooks[InstanceType, ReturnType] {
    private val beforeHooksBuffer = Buffer[BeforeHookFunction[InstanceType]]()
    private val afterHooksBuffer = Buffer[AfterHookFunction[InstanceType, ReturnType]]()
    def before(f: Seq[BeforeHookFunction[InstanceType]]) = beforeHooksBuffer ++= f
    def after(f: Seq[AfterHookFunction[InstanceType, ReturnType]]) = afterHooksBuffer ++= f
    def beforeHooks: Iterator[BeforeHookFunction[InstanceType]] = beforeHooksBuffer.toIterator
    def afterHooks: Iterator[AfterHookFunction[InstanceType, ReturnType]] = afterHooksBuffer.toIterator
  }
  
  sealed trait SaveHook {
    
    type SaveInstanceType
    type SaveReturnType
    
    private val saveHooks = new Hooks[SaveInstanceType, SaveReturnType]()
    
    protected def beforeSave(f: BeforeHookFunction[SaveInstanceType]*) = saveHooks.before(f)
    protected def afterSave(f: AfterHookFunction[SaveInstanceType, SaveReturnType]*) = saveHooks.after(f)
    protected def beforeSaveHooks: Iterator[BeforeHookFunction[SaveInstanceType]] = saveHooks.beforeHooks
    protected def afterSaveHooks: Iterator[AfterHookFunction[SaveInstanceType, SaveReturnType]] = saveHooks.afterHooks
  }

  sealed trait UpdateHook {
    
    type UpdateInstanceType
    type UpdateReturnType
    
    private val updateHooks = new Hooks[UpdateInstanceType, UpdateReturnType]()
    
    protected def beforeUpdate(f: BeforeHookFunction[UpdateInstanceType]*) = updateHooks.before(f)
    protected def afterUpdate(f: AfterHookFunction[UpdateInstanceType, UpdateReturnType]*) = updateHooks.after(f)
    protected def beforeUpdateHooks: Iterator[BeforeHookFunction[UpdateInstanceType]] = updateHooks.beforeHooks
    protected def afterUpdateHooks: Iterator[AfterHookFunction[UpdateInstanceType, UpdateReturnType]] = updateHooks.afterHooks
  }

  sealed trait CreateHook {
    
    type CreateInstanceType
    type CreateReturnType
    
    private val createHooks = new Hooks[CreateInstanceType, CreateReturnType]()
    
    protected def beforeCreate(f: BeforeHookFunction[CreateInstanceType]*) = createHooks.before(f)
    protected def afterCreate(f: AfterHookFunction[CreateInstanceType, CreateReturnType]*) = createHooks.after(f)
    protected def beforeCreateHooks: Iterator[BeforeHookFunction[CreateInstanceType]] = createHooks.beforeHooks
    protected def afterCreateHooks: Iterator[AfterHookFunction[CreateInstanceType, CreateReturnType]] = createHooks.afterHooks
  }

  sealed trait DeleteHook {
    
    type DeleteInstanceType
    type DeleteReturnType
    
    private val deleteHooks = new Hooks[DeleteInstanceType, DeleteReturnType]()
    
    protected def beforeDelete(f: BeforeHookFunction[DeleteInstanceType]*) = deleteHooks.before(f)
    protected def afterDelete(f: AfterHookFunction[DeleteInstanceType, DeleteReturnType]*) = deleteHooks.after(f)
    protected def beforeDeleteHooks: Iterator[BeforeHookFunction[DeleteInstanceType]] = deleteHooks.beforeHooks
    protected def afterDeleteHooks: Iterator[AfterHookFunction[DeleteInstanceType, DeleteReturnType]] = deleteHooks.afterHooks
  }

  trait Creatable[T] extends SaveHook with CreateHook {
  
    type CreatableReturnType
    override type SaveInstanceType = T
    override type CreateInstanceType = T
    override type SaveReturnType = CreatableReturnType
    override type CreateReturnType = CreatableReturnType
    
    def create(instance: T): CreatableReturnType = {
      beforeCreateHooks.foreach(_(instance))
      beforeSaveHooks.foreach(_(instance))
      val result = doCreate(instance)
      afterSaveHooks.foreach(_(instance, result))
      afterCreateHooks.foreach(_(instance, result))
      result
    }
  
    protected def doCreate(instance: T): CreatableReturnType
  
  }

  trait Updatable[T] extends SaveHook with UpdateHook {
    
    type UpdatableReturnType
    override type UpdateInstanceType = T
    override type SaveInstanceType = T
    override type UpdateReturnType = UpdatableReturnType
    override type SaveReturnType = UpdatableReturnType
    
    def update(instance: T): UpdatableReturnType = {
      beforeUpdateHooks.foreach(_(instance))
      beforeSaveHooks.foreach(_(instance))
      val result = doUpdate(instance)
      afterSaveHooks.foreach(_(instance, result))
      afterUpdateHooks.foreach(_(instance, result))
      result
    }
  
    protected def doUpdate(instance: T): UpdatableReturnType
  }

  trait Deletable[T] extends DeleteHook {
    
    type DeletableReturnType
    override type DeleteInstanceType = T
    override type DeleteReturnType = DeletableReturnType
    
    def delete(instance: T): DeletableReturnType = {
      beforeDeleteHooks.foreach(_(instance))
      val result  = doDelete(instance)
      afterDeleteHooks.foreach(_(instance, result))
      result
    }
  
    protected def doDelete(instance: T): DeletableReturnType
  }

  trait Crud[T, U] extends Creatable[T] with Updatable[T] with Deletable[T] {
    override type DeletableReturnType = U
    override type CreatableReturnType = Option[T]
    override type UpdatableReturnType = Option[T]
  }
  
}