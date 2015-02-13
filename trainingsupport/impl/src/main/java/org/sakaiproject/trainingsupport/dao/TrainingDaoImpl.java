package org.sakaiproject.trainingsupport.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;
import org.sakaiproject.trainingsupport.bean.TrainingJobTitleBean;
import org.sakaiproject.trainingsupport.bean.TrainingSectionRosterBean;
import org.sakaiproject.trainingsupport.model.TrainingJobTitle;
import org.sakaiproject.trainingsupport.model.TrainingRoster;
import org.sakaiproject.trainingsupport.model.TrainingRosterDiff;
import org.sakaiproject.trainingsupport.model.TrainingRosterJobTitle;
import org.sakaiproject.trainingsupport.model.TrainingSectionOrder;
import org.sakaiproject.trainingsupport.model.TrainingStatus;
import org.sakaiproject.trainingsupport.util.Constant;
import org.springframework.orm.hibernate3.HibernateCallback;

import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.component.cover.ComponentManager;
public class TrainingDaoImpl  extends HibernateGeneralGenericDao implements TrainingDao {

	private static final Logger log = Logger.getLogger(TrainingDaoImpl.class);

	private SqlService M_sql = (SqlService) ComponentManager.get(SqlService.class.getName());
	   public void init() {
	      log.debug("init");
	   }

	   public List<TrainingStatus> findStatuses(final String siteId, final String sectionId){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findStatuses");
				   query.setParameter("siteId", siteId);
				   query.setParameter("sectionId", sectionId);
				   
				   List<TrainingStatus> trainingStatusRequestList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (trainingStatusRequestList != null && (! trainingStatusRequestList.isEmpty())){
					   Set<TrainingStatus> trainingStatusRequestSet = new LinkedHashSet<TrainingStatus>(trainingStatusRequestList);
					   trainingStatusRequestList.clear();
					   trainingStatusRequestList.addAll(trainingStatusRequestSet);
				   }
				   return trainingStatusRequestList;
			   }
		   };
		   return (List<TrainingStatus>)getHibernateTemplate().execute(hc);
	   }
	   
	   public List<TrainingStatus> findStatusesBySiteId(final String siteId){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findStatusesBySiteId");
				   query.setParameter("siteId", siteId);
				   
				   List<TrainingStatus> trainingStatusRequestList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (trainingStatusRequestList != null && (! trainingStatusRequestList.isEmpty())){
					   Set<TrainingStatus> trainingStatusRequestSet = new LinkedHashSet<TrainingStatus>(trainingStatusRequestList);
					   trainingStatusRequestList.clear();
					   trainingStatusRequestList.addAll(trainingStatusRequestSet);
				   }
				   return trainingStatusRequestList;
			   }
		   };
		   return (List<TrainingStatus>)getHibernateTemplate().execute(hc);
	   }
	   
	   public List<TrainingStatus> findStatusesByUserEid(final String siteId, final String userEid){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findStatusesByUserEid");
				   query.setParameter("siteId", siteId);
				   query.setParameter("userEid", userEid);
				   
				   List<TrainingStatus> trainingStatusRequestList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (trainingStatusRequestList != null && (! trainingStatusRequestList.isEmpty())){
					   Set<TrainingStatus> trainingStatusRequestSet = new LinkedHashSet<TrainingStatus>(trainingStatusRequestList);
					   trainingStatusRequestList.clear();
					   trainingStatusRequestList.addAll(trainingStatusRequestSet);
				   }
				   return trainingStatusRequestList;
			   }
		   };
		   return (List<TrainingStatus>)getHibernateTemplate().execute(hc);
	   }

	   public List<TrainingRoster> findTrainingRostersBySectionId(final String sectionId){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findRosterBySectionId");
				   query.setParameter("sectionId", sectionId);

				   List<TrainingRoster> trainingRosterList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   /*if (trainingRosterList != null){
	   					   Set<TrainingRoster> trainingRosterSet = new LinkedHashSet<TrainingRoster>(trainingRosterList);
	   					   trainingRosterList.clear();
	   					   trainingRosterList.addAll(trainingRosterSet);
	   				   }*/
				   return trainingRosterList;
			   }
		   };
		   return (List<TrainingRoster>)getHibernateTemplate().execute(hc);
	   }

	   public TrainingRoster findTrainingRosterBySectionId(final String sectionId){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findRosterBySectionId");
				   query.setParameter("sectionId", sectionId);

				   List<TrainingRoster> trainingRosterList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (trainingRosterList != null && (! trainingRosterList.isEmpty())){
					   Set<TrainingRoster> trainingRosterSet = new LinkedHashSet<TrainingRoster>(trainingRosterList);
					   trainingRosterList.clear();
					   trainingRosterList.addAll(trainingRosterSet);
					   return trainingRosterList.get(0);
				   }
				   return null;
			   }
		   };
		   return (TrainingRoster)getHibernateTemplate().execute(hc);
	   }
	   
	   public List<TrainingStatus> findStatusesByOnlyUserEid(final String userEid){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findStatusesByOnlyUserEid");
				   query.setParameter("userEid", userEid);
				   
				   List<TrainingStatus> trainingStatusRequestList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (trainingStatusRequestList != null && (! trainingStatusRequestList.isEmpty())){
					   Set<TrainingStatus> trainingStatusRequestSet = new LinkedHashSet<TrainingStatus>(trainingStatusRequestList);
					   trainingStatusRequestList.clear();
					   trainingStatusRequestList.addAll(trainingStatusRequestSet);
				   }
				   return trainingStatusRequestList;
			   }
		   };
		   return (List<TrainingStatus>)getHibernateTemplate().execute(hc);
	   }
	   
	   public Date findLastUpadteDateOfStatusBySiteId(final String siteId){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findStatusesBySiteId");
				   query.setParameter("siteId", siteId);
				   List<TrainingStatus> trainingStatusRequestList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (trainingStatusRequestList != null && (! trainingStatusRequestList.isEmpty())){
					   return trainingStatusRequestList.get(0).getStatusUpdateDate();
				   }
				   return null;
			   }
		   };
		   return (Date)getHibernateTemplate().execute(hc);
	   }
	   
	   public TrainingSectionOrder findSectionOrderBySectionId(final String sectionId){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findSectionOrderBySectionId");
				   query.setParameter("sectionId", sectionId);
				   
				   List<TrainingSectionOrder> resultList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (resultList != null && (! resultList.isEmpty())){
					   Set<TrainingSectionOrder> resultSet = new LinkedHashSet<TrainingSectionOrder>(resultList);
					   resultList.clear();
					   resultList.addAll(resultSet);
					   return resultList.get(0);
				   }
				   return null;
			   }
		   };
		   return (TrainingSectionOrder)getHibernateTemplate().execute(hc);
	   }

	   public List<TrainingSectionOrder> findSectionOrderBySiteId(final String siteId){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findSectionOrderBySiteId");
				   query.setParameter("siteId", siteId);
				   
				   List<TrainingSectionOrder> resultList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (resultList != null){
					   Set<TrainingSectionOrder> resultSet = new LinkedHashSet<TrainingSectionOrder>(resultList);
					   resultList.clear();
					   resultList.addAll(resultSet);
				   }
				   return resultList;
			   }
		   };
		   return (List<TrainingSectionOrder>)getHibernateTemplate().execute(hc);
	   }

	   public List<TrainingJobTitle> findJobTitleByPattern(final String jobTitlePattern){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findJobtitleByPattern");
				   query.setParameter("jobTitlePattern", jobTitlePattern);
				   
				   List<TrainingJobTitle> resultList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (resultList != null){
					   Set<TrainingJobTitle> resultSet = new LinkedHashSet<TrainingJobTitle>(resultList);
					   resultList.clear();
					   resultList.addAll(resultSet);
				   }
				   return resultList;
			   }
		   };
		   return(List<TrainingJobTitle>)getHibernateTemplate().executeFind(hc);
	   }
	
	@Override
	public List<TrainingRosterDiff> findRosterDiffBySection(final String sectionId){
		HibernateCallback hc = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.getNamedQuery("findRosterDiffBySection");
				query.setParameter("sectionId", sectionId);
				return query.list();
			}
		};
		return (List<TrainingRosterDiff>) getHibernateTemplate().executeFind(hc);
	}
	
	@Override
	public TrainingRosterJobTitle findRosterJobTitleBySectionId(final String sectionId,final String siteId){
		HibernateCallback hc = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.getNamedQuery("findRosterJobTitleBySectionId");
				query.setParameter("sectionId", sectionId);
				query.setParameter("siteId", siteId);
				return query.uniqueResult();
			}
		};
		return (TrainingRosterJobTitle) getHibernateTemplate().execute(hc);
	}
	
	@Override
	public int deleteOutdatedRosterBase(final String siteId){
		
		final Long latestVersion = (Long) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.getNamedQuery("getLatestVersion");
				query.setParameter("siteId", siteId);
				return query.uniqueResult();
			}
		});
		
		if (latestVersion == null) {
			return 0;
		}
		
		HibernateCallback hc = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.getNamedQuery("deleteOutdatedRosterBase");
				query.setParameter("siteId", siteId);
				query.setParameter("latestVersion", latestVersion);
				return query.executeUpdate();
			}
		};
		return (Integer) getHibernateTemplate().execute(hc);
	}
	
	@Override
	public int deleteRosterDiff(final String siteId) {
		HibernateCallback hc = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.getNamedQuery("deleteRosterDiff");
				query.setParameter("siteId", siteId);
				return query.executeUpdate();
			}
		};
		return (Integer) getHibernateTemplate().execute(hc);
	}
	
	@Override
	public int createRosterDiff(final String siteId, final long currentVersion) {
		final String insertQuery =
				"insert into org.sakaiproject.trainingsupport.model.TrainingRosterDiff " +
				"    (siteId, sectionId, userEid, title, title1, title2, title3, diffType, information) ";
		
		final String selectUserDiff1 =
				"select cur.siteId, cur.sectionId, cur.userEid, " +
				"       cur.title, cur.title1, cur.title2, cur.title3, " +
				"       cast(:diffType as string), cast(null as string) " +
				"  from org.sakaiproject.trainingsupport.model.TrainingRosterBase as cur " +
				" where cur.siteId = :siteId " +
				"   and cur.version = :version " +
				"   and cur.userEid not in ( " +
				"           select last.userEid " +
				"             from org.sakaiproject.trainingsupport.model.TrainingRosterBase as last " +
				"            where last.siteId = :siteId " +
				"              and last.version <> :version) ";
		
		final String selectUserDiff2 =
				"select last.siteId, last.sectionId, last.userEid, " +
				"       last.title, last.title1, last.title2, last.title3, " +
				"       cast(:diffType as string), cast(null as string) " +
				"  from org.sakaiproject.trainingsupport.model.TrainingRosterBase as last " +
				" where last.siteId = :siteId " +
				"   and last.version <> :version " +
				"   and last.userEid not in ( " +
				"           select cur.userEid " +
				"             from org.sakaiproject.trainingsupport.model.TrainingRosterBase as cur " +
				"            where cur.siteId = :siteId " +
				"              and cur.version = :version) ";
		
		final String selectSectionDiff =
				"select cur.siteId, cur.sectionId, cur.userEid, " +
				"       cur.title, cur.title1, cur.title2, cur.title3, " +
				"       cast(:diffType as string), last.sectionId " +
				"  from org.sakaiproject.trainingsupport.model.TrainingRosterBase as cur, " +
				"       org.sakaiproject.trainingsupport.model.TrainingRosterBase as last " +
				" where cur.siteId = :siteId " +
				"   and cur.version = :version " +
				"   and cur.siteId = last.siteId " +
				"   and cur.userEid = last.userEid " +
				"   and cur.sectionId <> last.sectionId " +
				"   and cur.version <> last.version ";
		
		final String selectJobTitleDiff =
				"select cur.siteId, cur.sectionId, cur.userEid, " +
				"       cur.title, cur.title1, cur.title2, cur.title3, " +
				"       cast(:diffType as string), " +
				"       case " +
				"         when (cast(:jobTitlePattern as string) = 'title') then last.title " +
				"         when (cast(:jobTitlePattern as string) = 'title1') then last.title1 " +
				"         when (cast(:jobTitlePattern as string) = 'title2') then last.title2 " +
				"         when (cast(:jobTitlePattern as string) = 'title3') then last.title3 " +
				"         else false " +
				"       end " +
				"  from org.sakaiproject.trainingsupport.model.TrainingRosterBase as cur, " +
				"       org.sakaiproject.trainingsupport.model.TrainingRosterBase as last " +
				" where cur.siteId = :siteId " +
				"   and cur.sectionId = :sectionId " +
				"   and cur.version = :version " +
				"   and cur.siteId = last.siteId " +
				"   and cur.userEid = last.userEid " +
				"   and cur.sectionId = last.sectionId " +
				"   and (case " +
				"         when (cast(:jobTitlePattern as string) = 'title') and (cur.title <> last.title) then true " +
				"         when (cast(:jobTitlePattern as string) = 'title1') and (cur.title1 <> last.title1) then true " +
				"         when (cast(:jobTitlePattern as string) = 'title2') and (cur.title2 <> last.title2) then true " +
				"         when (cast(:jobTitlePattern as string) = 'title3') and (cur.title3 <> last.title3) then true " +
				"         else false " +
				"       end) = true " +
				"   and cur.version <> last.version ";
		
		final String selectJobTitlePattern = 
				"select trainingRosterJobTitle.sectionId, " +
				"       max(jobTitles.jobTitlePattern) " +
				"  from TrainingRosterJobTitle trainingRosterJobTitle " +
				"       join trainingRosterJobTitle.jobTitles jobTitles " +
				" where trainingRosterJobTitle.siteId = :siteId " +
				" group by trainingRosterJobTitle.sectionId ";
		
		@SuppressWarnings("unchecked")
		List<Object[]> jobTitleList = (List<Object[]>) getHibernateTemplate().execute(new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(selectJobTitlePattern);
				query.setParameter("siteId", siteId);
				return query.list();
			}
		});
		
		int insertCount = 0;
		
		insertCount += (Integer) getHibernateTemplate().execute(new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(insertQuery + selectUserDiff1);
				query.setParameter("siteId", siteId);
				query.setParameter("version", currentVersion);
				query.setParameter("diffType", Constant.ROSTER_DIFF_TYPE_ADDED);
				return query.executeUpdate();
			}
		});

		insertCount += (Integer) getHibernateTemplate().execute(new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(insertQuery + selectUserDiff2);
				query.setParameter("siteId", siteId);
				query.setParameter("version", currentVersion);
				query.setParameter("diffType", Constant.ROSTER_DIFF_TYPE_REMOVED);
				return query.executeUpdate();
			}
		});

		insertCount += (Integer) getHibernateTemplate().execute(new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(insertQuery + selectSectionDiff);
				query.setParameter("siteId", siteId);
				query.setParameter("version", currentVersion);
				query.setParameter("diffType", Constant.ROSTER_DIFF_TYPE_TRANSFERRED);
				return query.executeUpdate();
			}
		});
		
		for (Object[] jobTitle : jobTitleList) {
			final String sectionId = (String) jobTitle[0];
			final String jobTitlePattern = (String) jobTitle[1];
			insertCount += (Integer) getHibernateTemplate().execute(new HibernateCallback(){
				@Override
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Query query = session.createQuery(insertQuery + selectJobTitleDiff);
					query.setParameter("siteId", siteId);
					query.setParameter("sectionId", sectionId);
					query.setParameter("version", currentVersion);
					query.setParameter("diffType", Constant.ROSTER_DIFF_TYPE_JOB_TITLE_CHANGED);
					query.setParameter("jobTitlePattern", jobTitlePattern);
					return query.executeUpdate();
				}
			});
		}
		
		return insertCount;
	}

	public List<TrainingStatus> getAddDeleteList(final String currentSiteId, final String sectionId, final String listType){
		HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findRosterListByListType");
				   query.setParameter("siteId", currentSiteId);
				   query.setParameter("sectionId", sectionId);
				   query.setParameter("listType", listType);

				   List<TrainingStatus> resultList = query.list();
				   //if (resultList != null && (! resultList.isEmpty())){
				//	   Set<TrainingStatus> trainingStatusRequestSet = new LinkedHashSet<TrainingStatus>(trainingStatusRequestList);
					//   trainingStatusRequestList.clear();
				//	   trainingStatusRequestList.addAll(trainingStatusRequestSet);
				   //}
				   return resultList;
			   }
		   };
		   return (List<TrainingStatus>)getHibernateTemplate().execute(hc);
	}

	public TrainingJobTitle findJobTitleById(final long id) {

		HibernateCallback hc = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.getNamedQuery("findJobtitleById");
				query.setParameter("id", id);
				return query.uniqueResult();
			}
		};
		return (TrainingJobTitle) getHibernateTemplate().execute(hc);


	}

	@Override
	public int countRosterList(final String siteId, final String sectionId, final String listType) {
		int insertCount = (Integer) getHibernateTemplate().execute(new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.getNamedQuery("countRosterList");
				query.setParameter("siteId", siteId);
				query.setParameter("sectionId", sectionId);
				query.setParameter("listType", listType);
				return query.uniqueResult();
			}
		});
		return insertCount;
	}
}
