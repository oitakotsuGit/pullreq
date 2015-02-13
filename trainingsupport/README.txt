1. Realm 設定

  kernel/kernel-impl/src/main/sql/{hsqldb, mysql, oracle}/sakai_realm.sql

2. Site Setup の設定

 2.1 site-manage/site-manage-tool/tool/src/webapp/tools/sakai.sitesetup.xml に以下を修正:

  <configuration name="siteTypes" value="course,project,portfolio,training,alertnote" />
  
  <configuration name="publicChangeableSiteTypes" value="project,training,alertnote" />

 2.2 各ツールのツール設定ファイルで登録対象のサイト種別に追加:

  Lesson, Resource, Gradebook, Samigo, Site Info, Section Info, Mail Sender

  lessonbuilder/tool/src/webapp/tools/sakai.lessonbuildertool.xml 

		<category name="training" />

  content/content-tool/tool/src/webapp/tools/sakai.resources.xml

		<category name="training" />

  gradebook/app/sakai-tool/src/webapp/tools/sakai.gradebook.tool.xml

		<category name="training" />

  samigo/samigo-app/sakai-samigo/webapp/tools/sakai.samigo.tool.xml

     <category name="training" />

  site-manage/site-manage-tool/tool/src/webapp/tools/sakai.siteinfo.xml

		<category name="training" />
		<category name="alertnote" />

  sections/sections-app/src/webapp/sakai/tools/sakai.sections.xml 

		<category name="training" />
		<category name="alertnote" />

  mailsender/tool/src/webapp/tools/sakai.mailsender.xml

		<category name="training" />
		<category name="alertnote" />

3. 言語プロパティの追加

  site-manage/site-manage-tool/tool/src/bundle/sitesetupgeneric.properties  
  site-manage/site-manage-tool/tool/src/bundle/sitesetupgeneric_ja.properties  

    sitetype.setuptraining = Training
    sitetype.setupalertnote = Alertnote

    sitegen.roledescription.TrainingServiceAdministrator = Can read, revise, delete and add both content and participants to a site. 
    sitegen.roledescription.TrainingServiceOrganizer = Can read, revise, delete and add both content and participants to a site. 
    sitegen.roledescription.TrainingDepartmentManager = Can read, revise, delete and add participants to a site. 
    sitegen.roledescription.Trainee = Can read content in a site where appropriate. 

    sitegen.roledescription.AlertnoteServiceAdministrator = Can read, revise, delete and add both content and participants to a site. 
    sitegen.roledescription.AlertnoteServiceOrganizer = Can read, revise, delete and add both content and participants to a site. 
    sitegen.roledescription.AlertnoteDepartmentManager = Can read, revise, delete and add participants to a site. 
    sitegen.roledescription.AlertnoteReceiver = Can read content in a site where appropriate. 

    sitegen.role.TrainingServiceAdministrator = Training Service Administrator
    sitegen.role.TrainingOrganizer = Training Organizer
    sitegen.role.TrainingDepartmentManager = Training Department Manager
    sitegen.role.Trainee = Trainee

    sitegen.role.AlertnoteServiceAdministrator = Alertnote Service Administrator
    sitegen.role.AlertnoteOrganizer = Alertnote Organizer
    sitegen.role.AlertnoteDepartmentManager = Alertnote Department Manager
    sitegen.role.AlertnoteReceiver = Alertnote Receiver

4. デフォルトツール順序の設定

    kernel/component-manager/src/main/bundle/org/sakaiproject/config/toolOrder.xml

	<category name="training">
		<tool id = "home" selected = "true" />
		<tool id = "sakai.lessonbuildertool" />
		<tool id = "sakai.resources" />
		<tool id = "sakai.samigo" />
		<tool id = "sakai.gradebook.tool" />
		<tool id = "sakai.mailtool" />
		<tool id = "sakai.sections" required = "true" />
		<tool id = "sakai.siteinfo" required = "true" />
		<tool id = "sakai.trainingsupport" required = "true" />
	</category>

	<category name="alertnote">
		<tool id = "home" selected = "true" />
		<tool id = "sakai.mailtool" />
		<tool id = "sakai.sections" required = "true" />
		<tool id = "sakai.siteinfo" required = "true" />
		<tool id = "sakai.alertnote" required = "true" />
	</category>

        <category name="myworkspace">
                <tool id = "home" selected = "true" />
                <tool id = "sakai.sitesetup" required = "true" />
                <tool id = "sakai.membership" required="true" />
                <tool id = "sakai.preferences" required="true" />
                <tool id = "sakai.alertnote" selected="true" />       ← 追加
                <tool id = "sakai.trainingsupport" selected="true" /> ← 追加
        </category>

5. $SAKAI_SRC/pom.xml への追加

               <module>trainingsupport</module>
               <module>alertnote</module>

6. trainingsupport, alertnote ツールのツール設定ファイルで登録対象のサイト種別に追加:

  6.1 trainingsupport は Training Site と My Workspace

     trainingsupport/tool/src/webapp/tools/sakai.trainingsupport.xml

                <category name="training" />
                <category name="myworkspace" />

  6.2 alertnote は Alertnote Site と My Workspace

     alertnote/tool/src/webapp/tools/sakai.alertnote.xml 

                <category name="alertnote" />
                <category name="myworkspace" />

7. Refactering

# Wicket Version
# https://confluence.sakaiproject.org/display/BOOT/Sakai+Wicket+Maven+Archetype
mvn archetype:generate -DarchetypeGroupId=org.sakaiproject.maven-archetype -DarchetypeArtifactId=sakai-wicket-maven-archetype -DarchetypeVersion=1.2 -DarchetypeRepository=https://source.sakaiproject.org/maven2/

# Spring Version
mvn archetype:generate -DarchetypeGroupId=org.sakaiproject.maven-archetype -DarchetypeArtifactId=sakai-spring-maven-archetype -DarchetypeVersion=1.2 -DarchetypeRepository=https://source.sakaiproject.org/maven2/

8. jldap-beans.xml

  8.1 PandaDirectoryService の使用
  8.2 AttributeMapper の設定 (affiliate を忘れずに)

9. テーブル作成

 Oracle:
   INSERT INTO cm_sec_category_t(CAT_CODE, CAT_DESCR) VALUES ('training.tool.section', '部局');

 MySQL:
   INSERT INTO sakai.cm_sec_category_t(CAT_CODE, CAT_DESCR) VALUES ('training.tool.section', '部局');

   ALTER TABLE training_section_order ADD INDEX training_secorder_siteid(site_id);
   ALTER TABLE training_section_order ADD INDEX training_secorder_secid(section_id);
   ALTER TABLE training_job_title ADD INDEX training_jobtl_pattern(job_title_pattern(10));

ALTER TABLE TRAINING_JOB_TITLE ADD `job_title_pattern` varchar(255);
ALTER TABLE TRAINING_JOB_TITLE ADD `view_rank` int(11) DEFAULT NULL;
ALTER TABLE TRAINING_JOB_TITLE ADD NIQUE KEY `job_title` (`job_title`,`job_title_pattern`);

10. authz ツールでの権限表示のリソースバンドル

