-- CreateTable
CREATE TABLE "Service" (
    "pkey" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "enable" BOOLEAN NOT NULL DEFAULT true,
    "debug" BOOLEAN NOT NULL DEFAULT false,
    "hook" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "description" TEXT NOT NULL,
    "id" TEXT NOT NULL,
    "usageRequirements" TEXT NOT NULL,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- CreateTable
CREATE TABLE "Prefetch" (
    "pkey" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "label" TEXT NOT NULL,
    "parameta" TEXT NOT NULL,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- CreateTable
CREATE TABLE "Extention" (
    "pkey" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "label" TEXT NOT NULL,
    "parameta" TEXT NOT NULL,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- CreateTable
CREATE TABLE "Cdshost" (
    "pkey" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "url" TEXT NOT NULL,
    "username" TEXT NOT NULL,
    "password" TEXT NOT NULL,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- CreateTable
CREATE TABLE "_PrefetchToService" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL,
    CONSTRAINT "_PrefetchToService_A_fkey" FOREIGN KEY ("A") REFERENCES "Prefetch" ("pkey") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "_PrefetchToService_B_fkey" FOREIGN KEY ("B") REFERENCES "Service" ("pkey") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "_ExtentionToService" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL,
    CONSTRAINT "_ExtentionToService_A_fkey" FOREIGN KEY ("A") REFERENCES "Extention" ("pkey") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "_ExtentionToService_B_fkey" FOREIGN KEY ("B") REFERENCES "Service" ("pkey") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "_CdshostToService" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL,
    CONSTRAINT "_CdshostToService_A_fkey" FOREIGN KEY ("A") REFERENCES "Cdshost" ("pkey") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "_CdshostToService_B_fkey" FOREIGN KEY ("B") REFERENCES "Service" ("pkey") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateIndex
CREATE UNIQUE INDEX "_PrefetchToService_AB_unique" ON "_PrefetchToService"("A", "B");

-- CreateIndex
CREATE INDEX "_PrefetchToService_B_index" ON "_PrefetchToService"("B");

-- CreateIndex
CREATE UNIQUE INDEX "_ExtentionToService_AB_unique" ON "_ExtentionToService"("A", "B");

-- CreateIndex
CREATE INDEX "_ExtentionToService_B_index" ON "_ExtentionToService"("B");

-- CreateIndex
CREATE UNIQUE INDEX "_CdshostToService_AB_unique" ON "_CdshostToService"("A", "B");

-- CreateIndex
CREATE INDEX "_CdshostToService_B_index" ON "_CdshostToService"("B");
