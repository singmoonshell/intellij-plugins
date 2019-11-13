// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.angular2.entities.ivy;

import com.intellij.lang.javascript.psi.JSTypeUtils;
import com.intellij.lang.javascript.psi.ecma6.TypeScriptField;
import com.intellij.lang.javascript.psi.ecma6.TypeScriptTypeofType;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.util.containers.JBIterable;
import org.angular2.entities.*;
import org.angular2.entities.Angular2ModuleResolver.ResolvedEntitiesList;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.intellij.lang.javascript.psi.types.TypeScriptTypeOfJSTypeImpl.getTypeOfResultElements;
import static org.angular2.entities.ivy.Angular2IvyUtil.MODULE_DEF;

public class Angular2IvyModule extends Angular2IvyEntity implements Angular2Module {

  private final Angular2ModuleResolver<TypeScriptField> myModuleResolver = new Angular2ModuleResolver<>(
    () -> myDefField, Angular2IvyModule::collectSymbols);


  public Angular2IvyModule(@NotNull TypeScriptField defField) {
    super(defField);
  }

  @Override
  @NotNull
  public Set<Angular2Declaration> getDeclarations() {
    return myModuleResolver.getDeclarations();
  }

  @Override
  @NotNull
  public Set<Angular2Module> getImports() {
    return myModuleResolver.getImports();
  }

  @Override
  @NotNull
  public Set<Angular2Entity> getExports() {
    return myModuleResolver.getExports();
  }

  @NotNull
  @Override
  public Set<Angular2Declaration> getAllExportedDeclarations() {
    return myModuleResolver.getAllExportedDeclarations();
  }

  @Override
  public boolean isScopeFullyResolved() {
    return myModuleResolver.isScopeFullyResolved();
  }

  @Override
  public boolean isPublic() {
    //noinspection HardCodedStringLiteral
    return !getName().startsWith("ɵ");
  }

  @Override
  public boolean areExportsFullyResolved() {
    return myModuleResolver.areExportsFullyResolved();
  }

  @Override
  public boolean areDeclarationsFullyResolved() {
    return myModuleResolver.areDeclarationsFullyResolved();
  }

  @NotNull
  private static <T extends Angular2Entity> Result<ResolvedEntitiesList<T>> collectSymbols(@NotNull TypeScriptField fieldDef,
                                                                                           @NotNull String propertyName,
                                                                                           @NotNull Class<T> symbolClazz) {
    List<TypeScriptTypeofType> types = MODULE_DEF.getTypesList(fieldDef, propertyName);
    if (types.isEmpty()) {
      return ResolvedEntitiesList.createResult(Collections.emptySet(), true, fieldDef);
    }
    Set<T> entities = new HashSet<>();
    boolean fullyResolved = true;
    for (TypeScriptTypeofType typeOfType: types) {
      String reference = typeOfType.getReferenceText();
      if (reference == null) {
        fullyResolved = false;
        continue;
      }
      T entity = JBIterable.from(getTypeOfResultElements(typeOfType, reference))
        .filterMap(el -> Angular2EntitiesProvider.getEntity(el))
        .filter(symbolClazz)
        .first();
      if (entity == null) {
        fullyResolved = false;
      } else {
        entities.add(entity);
      }
    }
    return ResolvedEntitiesList.createResult(entities, fullyResolved, JSTypeUtils.getTypeInvalidationDependency());
  }

}
